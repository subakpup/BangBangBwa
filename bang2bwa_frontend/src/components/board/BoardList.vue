<template>
  <div>
      <div class="board-header-row">
        
        <h2 class="board-title flex items-center flex-shrink-0">
          지역 게시판 
          <span class="text-sm font-normal text-gray-500 ml-2 whitespace-nowrap">
            ({{ totalCount }}개의 글)
          </span>
        </h2>
        
        <div class="flex flex-col md:flex-row gap-2 w-full md:w-auto">
          
          <div class="flex gap-2 w-full md:w-auto">
            <select v-model="searchParams.sidoNm" class="region-select flex-1 md:flex-none md:w-32">
              <option value="">시/도 전체</option>
              <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoName">
                {{ sido.sidoName }}
              </option>
            </select>

            <select v-model="searchParams.sggNm" class="region-select flex-1 md:flex-none md:w-32" :disabled="!searchParams.sidoNm">
              <option value="">구/군 전체</option>
              <option v-for="sgg in sggList" :key="sgg.gugunCode" :value="sgg.gugunName">
                {{ sgg.gugunName }}
              </option>
            </select>
          </div>
          
          <div class="search-box">
            <input 
              v-model="searchParams.keyword" 
              type="text" 
              class="input-base px-4 py-2 w-full md:w-48 h-[42px]" 
              placeholder="검색어를 입력하세요"
              @keyup.enter="handleSearch"
            />
            <button @click="handleSearch" class="btn-search flex-shrink-0" title="검색">
              <Search :size="20" />
            </button>
          </div>

          <button @click="router.push('/board/write')" class="btn-base flex items-center justify-center gap-2 whitespace-nowrap h-[42px] w-full md:w-auto">
            <PenTool :size="16" /> <span class="md:hidden lg:inline">글쓰기</span>
          </button>
        </div>
      </div>

      <div class="board-table-container min-h-[400px]">
        <table class="board-table min-w-[600px] md:min-w-full"> 
          <thead>
            <tr>
              <th class="board-th w-16 text-center">번호</th>
              <th class="board-th w-40">지역</th>
              <th class="board-th">제목</th>
              <th class="board-th w-24 md:w-32 text-center">작성자</th>
              <th class="board-th w-24 md:w-32 text-center">작성일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(post, index) in posts" :key="post.postId" class="board-tr" @click="goDetail(post.postId)">
              <td class="board-td text-center text-gray-400">
                {{ totalCount - ((searchParams.page - 1) * searchParams.size) - index }}
              </td>
              
              <td class="board-td">
                <span class="text-xs text-[#AE8B72] font-bold border border-[#CEAC93] px-2 py-0.5 rounded bg-[#FFFBE8] whitespace-nowrap">
                  {{ post.sidoNm }} {{ post.sggNm }}
                </span>
              </td>
              
              <td class="board-td font-medium text-gray-800 break-all">
                {{ post.title }}
                <span v-if="post.viewCnt > 100" class="text-xs text-red-400 ml-1">HOT</span>
              </td>
              
              <td class="board-td text-center text-gray-600 truncate max-w-[100px]">{{ post.name || '익명' }}</td>
              
              <td class="board-td text-center text-gray-500 text-xs whitespace-nowrap">
                {{ post.createdAt ? post.createdAt.split('T')[0] : '-' }}
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

      <div v-if="totalPages > 0" class="pagination-container overflow-x-auto py-2">
         <button @click="changePage(1)" :disabled="searchParams.page === 1" class="page-btn flex-shrink-0">
            <ChevronsLeft :size="16" />
         </button>
         <button @click="changePage(searchParams.page - 1)" :disabled="searchParams.page === 1" class="page-btn flex-shrink-0">
            <ChevronLeft :size="16" />
         </button>
         
         <button v-for="p in visiblePageNumbers" :key="p" @click="changePage(p)" class="page-btn flex-shrink-0" :class="{ 'active': searchParams.page === p }">
            {{ p }}
         </button>

         <button @click="changePage(searchParams.page + 1)" :disabled="searchParams.page === totalPages" class="page-btn flex-shrink-0">
            <ChevronRight :size="16" />
         </button>
         <button @click="changePage(totalPages)" :disabled="searchParams.page === totalPages" class="page-btn flex-shrink-0">
            <ChevronsRight :size="16" />
         </button>
      </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/boardApi'
import { getSidoList, getSggList } from '@/api/addressApi' // API 추가
import { PenTool, Search, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-vue-next'

const router = useRouter()

// === [상태 관리] ===
const sidoList = ref([]) // 시도 목록
const sggList = ref([])  // 구군 목록

const searchParams = ref({
  sidoNm: '',
  sggNm: '',
  keyword: '',
  page: 1,  
  size: 10  
})

const posts = ref([])       
const totalPages = ref(0)   
const totalCount = ref(0)   
const loading = ref(false)

// === [Computed: 페이지네이션 블록 계산] ===
const visiblePageNumbers = computed(() => {
  const blockSize = 5; 
  const currentBlock = Math.ceil(searchParams.value.page / blockSize);
  
  const startPage = (currentBlock - 1) * blockSize + 1;
  const endPage = Math.min(startPage + blockSize - 1, totalPages.value);
  
  const pages = [];
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
})

// === [Watch: 시/도 변경 시 구/군 목록 로드 및 검색] ===
watch(() => searchParams.value.sidoNm, async (newSidoName) => {
  // 1. 하위 선택 초기화
  searchParams.value.sggNm = '';
  searchParams.value.page = 1;
  sggList.value = [];

  // 2. 시도가 선택되었을 때만 구군 API 호출
  if (newSidoName) {
    // API 호출을 위해 이름에 해당하는 Code 찾기
    const selectedSido = sidoList.value.find(s => s.sidoName === newSidoName);
    if (selectedSido) {
      try {
        sggList.value = await getSggList(selectedSido.sidoCode);
      } catch (error) {
        console.error("구군 로드 실패:", error);
      }
    }
  }
  
  // 3. 변경된 조건으로 게시글 검색
  fetchPosts();
})

// === [Watch: 구/군 변경 시 검색] ===
watch(() => searchParams.value.sggNm, () => {
    searchParams.value.page = 1;
    fetchPosts();
});

// === [API 호출] ===
const fetchSidoList = async () => {
    try {
        sidoList.value = await getSidoList();
    } catch (error) {
        console.error("시도 로드 실패:", error);
    }
}

const fetchPosts = async () => {
  loading.value = true;
  try {
    const res = await getPostList(searchParams.value);
    
    if (res && res.success && res.data) {
      const { content, totalPage, totalCount: count } = res.data;
      
      posts.value = content || [];
      totalPages.value = totalPage || 0;
      totalCount.value = count || 0;
    } else {
      posts.value = [];
      totalPages.value = 0;
      totalCount.value = 0;
    }
  } catch (e) {
    console.error(e);
    posts.value = [];
  } finally {
    loading.value = false;
  }
}

// === [핸들러] ===
const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  searchParams.value.page = page;
  fetchPosts();
}

const handleSearch = () => {
  searchParams.value.page = 1;
  fetchPosts();
}

const goDetail = (id) => router.push(`/board/${id}`)

// 초기 로딩
onMounted(async () => {
    await fetchSidoList(); // 시도 목록 로드
    await fetchPosts();    // 게시글 목록 로드
})
</script>