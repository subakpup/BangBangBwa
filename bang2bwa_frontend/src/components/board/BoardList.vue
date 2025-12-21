<template>
<div>
    <div class="board-header-row items-end">
        <h2 class="board-title">지역 게시판 </h2>
        
        <div class="flex flex-col md:flex-row gap-2 w-full md:w-auto">
            <select v-model="searchParams.sidoNm" class="region-select">
                <option value="">시/도 전체</option>
                <option v-for="r in regionOptions" :key="r.sido" :value="r.sido">
                    {{ r.sido }}
                </option>
            </select>

            <select v-model="searchParams.sggNm" class="region-select" :disabled="!searchParams.sidoNm">
                <option value="">구/군 전체</option>
                <option v-for="sgg in filteredSggOptions" :key="sgg" :value="sgg">
                    {{ sgg }}
                </option>
            </select>
            
            <div class="relative">
                <input 
                    v-model="searchParams.keyword" 
                    type="text" 
                    class="input-base pl-3 pr-10 py-2 w-full md:w-48"
                    placeholder="검색어 입력"
                    @keyup.enter="fetchPosts"
                />
                <button @click="fetchPosts" class="absolute right-2 top-2 text-[#AE8B72]">
                    <Search :size="18" />
                </button>
            </div>

            <button @click="router.push('/board/write')" class="btn-base flex items-center justify-center gap-2 whitespace-nowrap">
                <PenTool :size="16" /> 글쓰기
            </button>
        </div>
    </div>

    <div class="board-table-container min-h-[400px]">
        <table class="board-table">
            <thead>
                <tr>
                    <th class="board-th w-16 text-center">번호</th>
                    <th class="board-th w-40">지역</th>
                    <th class="board-th">제목</th>
                    <th class="board-th w-32 text-center">작성자</th>
                    <th class="board-th w-32 text-center">작성일</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(post, index) in posts" :key="post.postId" class="board-tr" @click="goDetail(post.postId)">
                    <td class="board-td text-center text-gray-400">{{ posts.length - index }}</td>
                    
                    <td class="board-td">
                    <span class="text-xs text-[#AE8B72] font-bold border border-[#CEAC93] px-2 py-0.5 rounded bg-[#FFFBE8]">
                        {{ post.sidoNm }} {{ post.sggNm }}
                    </span>
                    </td>
                    
                    <td class="board-td font-medium text-gray-800">{{ post.title }}</td>
                    <td class="board-td text-center text-gray-600">{{ post.writerName || '익명' }}</td>
                    <td class="board-td text-center text-gray-500 text-xs">
                    {{ post.createdDate ? post.createdDate.split('T')[0] : '-' }}
                    </td>
                </tr>
                
                <tr v-if="posts.length === 0 && !loading">
                    <td colspan="5" class="p-10 text-center text-gray-400">
                    등록된 게시글이 없습니다.
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/boardApi'
import { PenTool, Search } from 'lucide-vue-next'

const router = useRouter()

// 1. 지역 데이터 (실제로는 공통 코드로 관리하면 좋습니다)
const regionOptions = [
  { sido: '서울특별시', sgg: ['강남구', '서초구', '송파구'] },
  { sido: '광주광역시', sgg: ['광산구', '동구', '서구', '남구', '북구'] },
  { sido: '전라남도', sgg: ['나주시', '목포시', '여수시'] }
]

// 2. 검색 상태 (PostSearchDto)
const searchParams = ref({
  sidoNm: '',
  sggNm: '',
  keyword: ''
})

const posts = ref([])
const loading = ref(false)

// 3. 시/도가 선택되면 해당 시/도의 구/군 목록만 보여줌
const filteredSggOptions = computed(() => {
  const selected = regionOptions.find(r => r.sido === searchParams.value.sidoNm);
  return selected ? selected.sgg : [];
})

// 시/도 변경 시 구/군 초기화
watch(() => searchParams.value.sidoNm, () => {
  searchParams.value.sggNm = '';
})

// 4. 게시글 목록 조회
const fetchPosts = async () => {
  loading.value = true;
  // API 호출 params: { sidoNm, sggNm, keyword }
  const res = await getPostList(searchParams.value);
  
  if (res && res.data) {
    posts.value = res.data; 
  } else {
    posts.value = [];
  }
  loading.value = false;
}

onMounted(() => fetchPosts())

const goDetail = (id) => router.push(`/board/${id}`)
</script>