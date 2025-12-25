<template>
<div>
    <h2 class="board-title mb-6">{{ isEditMode ? '게시글 수정' : '새 글 작성' }}</h2>
    
    <div class="post-container">
        <form @submit.prevent="handleSubmit">
            <div class="flex flex-col gap-4 mb-4">
                <div class="flex gap-2">
                    <select v-model="form.sidoNm" @change="handleSidoChange" class="region-select w-1/2" required>
                        <option value="" disabled>시/도 선택</option>
                        <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoName">
                            {{ sido.sidoName }}
                        </option>
                    </select>
                    
                    <select v-model="form.sggNm" class="region-select w-1/2" :disabled="!form.sidoNm" required>
                        <option value="" disabled>구/군 선택</option>
                        <option v-for="sgg in sggList" :key="sgg.gugunCode" :value="sgg.gugunName">
                            {{ sgg.gugunName }}
                        </option>
                    </select>
                </div>
            
                <input 
                    v-model="form.title"
                    type="text" 
                    class="input-base px-4 py-3 text-lg font-bold w-full" 
                    placeholder="제목을 입력하세요"
                    required
                />
            </div>

            <textarea 
                v-model="form.content"
                class="write-editor" 
                placeholder="내용을 작성해주세요."
                required
            ></textarea>

            <div class="flex gap-3 mt-6 justify-end">
                <button type="button" @click="router.back()" class="btn-base bg-gray-400 hover:bg-gray-500">
                취소
                </button>
                <button type="submit" class="btn-submit px-8">
                {{ isEditMode ? '수정 완료' : '등록 하기' }}
                </button>
            </div>
        </form>
    </div>
</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { writePost, getPostDetail, modifyPost } from '@/api/boardApi'
import { getSidoList, getSggList } from '@/api/addressApi' // 주소 API 추가

const router = useRouter()
const route = useRoute()

const isEditMode = ref(false)
const postId = route.params.id

// === [상태 관리] ===
const sidoList = ref([]) // 시도 목록
const sggList = ref([])  // 구군 목록

// 폼 데이터
const form = ref({
  sidoNm: '',
  sggNm: '',
  title: '',
  content: ''
})

// === [로직] ===

// 1. 시/도 목록 불러오기
const fetchSidoList = async () => {
    try {
        sidoList.value = await getSidoList();
    } catch (error) {
        console.error("시도 로드 실패:", error);
    }
}

// 2. 시/도 변경 시 구/군 목록 갱신 핸들러 (사용자가 직접 변경 시)
const handleSidoChange = async () => {
  // 구/군 선택 초기화
  form.value.sggNm = '';
  sggList.value = [];
  
  await loadSggData(form.value.sidoNm);
}

// 3. 구/군 데이터 로딩 헬퍼 함수
const loadSggData = async (sidoName) => {
    if (!sidoName) return;

    // 이름으로 코드 찾기
    const selectedSido = sidoList.value.find(s => s.sidoName === sidoName);
    if (selectedSido) {
        try {
            sggList.value = await getSggList(selectedSido.sidoCode);
        } catch (error) {
            console.error("구군 로드 실패:", error);
        }
    }
}

// === [라이프사이클] ===
onMounted(async () => {
  // 1. 먼저 시도 목록을 불러옵니다.
  await fetchSidoList();

  // 2. 수정 모드라면 상세 데이터를 불러옵니다.
  if (postId) {
    isEditMode.value = true;
    try {
        const res = await getPostDetail(postId);
        if (res && res.data) {
            const data = res.data;
            
            // 3. 기존 데이터의 시도에 맞는 구군 목록을 먼저 로드합니다.
            // (이 과정이 없으면 구군 드롭다운이 비어있게 됩니다)
            await loadSggData(data.sidoNm);

            // 4. 폼 데이터 채우기
            form.value = {
                sidoNm: data.sidoNm,
                sggNm: data.sggNm,
                title: data.title,
                content: data.content
            }
        }
    } catch (error) {
        console.error("게시글 로드 실패:", error);
        alert("게시글 정보를 불러오는데 실패했습니다.");
        router.back();
    }
  }
})

// === [제출 핸들러] ===
const handleSubmit = async () => {
  // 유효성 검사
  if (!form.value.sidoNm || !form.value.sggNm) {
    alert("지역을 선택해주세요.");
    return;
  }
  if (!form.value.title || !form.value.content) {
    alert("제목과 내용을 입력해주세요.");
    return;
  }

  try {
      let res;
      if (isEditMode.value) {
        // 수정 API
        res = await modifyPost(postId, form.value);
      } else {
        // 작성 API
        res = await writePost(form.value);
      }

      // 성공 체크 (백엔드 응답 구조에 따라 조정)
      if (res) {
        alert(isEditMode.value ? "수정되었습니다." : "등록되었습니다.");
        router.push('/board');
      }
  } catch (error) {
      console.error(error);
      alert("오류가 발생했습니다.");
  }
}
</script>