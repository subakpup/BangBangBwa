<template>
<div>
    <h2 class="board-title mb-6">{{ isEditMode ? '게시글 수정' : '새 글 작성' }}</h2>
    
    <div class="post-container">
        <form @submit.prevent="handleSubmit">
            <div class="flex flex-col gap-4 mb-4">
                <div class="flex gap-2">
                    <select v-model="form.sidoNm" @change="handleSidoChange" class="region-select w-1/2">
                        <option value="" disabled>시/도 선택</option>
                        <option v-for="r in regionOptions" :key="r.sido" :value="r.sido">
                            {{ r.sido }}
                        </option>
                    </select>
                    
                    <select v-model="form.sggNm" class="region-select w-1/2" :disabled="!form.sidoNm">
                        <option value="" disabled>구/군 선택</option>
                        <option v-for="sgg in filteredSggOptions" :key="sgg" :value="sgg">
                            {{ sgg }}
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
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { writePost, getPostDetail, modifyPost } from '@/api/boardApi'

const router = useRouter()
const route = useRoute()

const isEditMode = ref(false)
const postId = route.params.id

// 지역 데이터 (List와 동일하게 사용)
const regionOptions = [
  { sido: '서울특별시', sgg: ['강남구', '서초구', '송파구'] },
  { sido: '광주광역시', sgg: ['광산구', '동구', '서구', '남구', '북구'] },
  { sido: '전라남도', sgg: ['나주시', '목포시', '여수시'] }
]

// 폼 데이터 (PostRequestDto 매핑)
const form = ref({
  sidoNm: '',
  sggNm: '',
  title: '',
  content: ''
})

// 구/군 옵션 계산
const filteredSggOptions = computed(() => {
  const selected = regionOptions.find(r => r.sido === form.value.sidoNm);
  return selected ? selected.sgg : [];
})

// 시/도 변경 시 구/군 초기화 (단, 수정 모드에서 데이터 로딩 중일 땐 초기화 방지 로직 필요할 수 있음)
// 여기선 간단하게 사용자가 직접 바꿀 때만 초기화되도록 watch 대신 @change 이벤트 사용 권장
const handleSidoChange = () => {
  form.value.sggNm = '';
}

onMounted(async () => {
  // 수정 모드 진입 시 데이터 채우기
  if (postId) {
    isEditMode.value = true;
    const res = await getPostDetail(postId);
    if (res && res.data) {
      const data = res.data;
      form.value = {
        sidoNm: data.sidoNm,
        sggNm: data.sggNm,
        title: data.title,
        content: data.content
      }
    }
  }
})

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

  let res;
  if (isEditMode.value) {
    // 수정 API
    res = await modifyPost(postId, form.value);
  } else {
    // 작성 API
    res = await writePost(form.value);
  }

  if (res && (res.status === 200 || res.success)) {
    alert(isEditMode.value ? "수정되었습니다." : "등록되었습니다.");
    router.push('/board');
  } else {
    alert("오류가 발생했습니다.");
  }
}
</script>