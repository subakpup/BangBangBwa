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
              <option v-for="r in regionOptions" :key="r.sido" :value="r.sido">{{ r.sido }}</option>
            </select>

            <select v-model="searchParams.sggNm" class="region-select flex-1 md:flex-none md:w-32" :disabled="!searchParams.sidoNm">
              <option value="">구/군 전체</option>
              <option v-for="sgg in filteredSggOptions" :key="sgg" :value="sgg">{{ sgg }}</option>
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
import { PenTool, Search, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-vue-next'

const router = useRouter()

// === [상태 관리] ===
const regionOptions = [
  { sido: '서울특별시', sgg: ['강남구', '서초구', '송파구'] },
  { sido: '광주광역시', sgg: ['광산구', '동구', '서구', '남구', '북구'] },
  { sido: '전라남도', sgg: ['나주시', '목포시', '여수시'] }
]

const searchParams = ref({
  sidoNm: '',
  sggNm: '',
  keyword: '',
  page: 1,  // 현재 페이지 (1부터 시작)
  size: 10  // 페이지당 개수
})

const posts = ref([])       // 게시글 목록
const totalPages = ref(0)   // 전체 페이지 수
const totalCount = ref(0)   // 전체 게시글 수
const loading = ref(false)

// === [Computed: 구/군 필터] ===
const filteredSggOptions = computed(() => {
  const selected = regionOptions.find(r => r.sido === searchParams.value.sidoNm);
  return selected ? selected.sgg : [];
})

// === [Computed: 페이지네이션 블록 계산 (1~5, 6~10)] ===
const visiblePageNumbers = computed(() => {
  const blockSize = 5; // 한 번에 보여줄 페이지 버튼 개수
  const currentBlock = Math.ceil(searchParams.value.page / blockSize);
  
  const startPage = (currentBlock - 1) * blockSize + 1;
  const endPage = Math.min(startPage + blockSize - 1, totalPages.value);
  
  const pages = [];
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
})

// === [Watch: 시/도 변경 시 초기화] ===
watch(() => searchParams.value.sidoNm, () => {
  searchParams.value.sggNm = '';
  searchParams.value.page = 1;
  fetchPosts();
})

// === [API 호출] ===
const fetchPosts = async () => {
  loading.value = true;
  try {
    // boardApi.js에서 page-1 처리를 하므로 여기선 그대로 넘깁니다.
    const res = await getPostList(searchParams.value);
    
    // 백엔드: ApiResponse<PageResponse<PostListDto>>
    if (res && res.success && res.data) {
      // PageResponse 구조 해체
      const { content, totalPage, totalCount: count } = res.data;
      
      posts.value = content || [];
      totalPages.value = totalPage || 0;
      totalCount.value = count || 0;
    } else {
      // 실패 혹은 데이터 없음
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
// 페이지 변경
const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  searchParams.value.page = page;
  fetchPosts();
}

// 검색 (1페이지로 리셋)
const handleSearch = () => {
  searchParams.value.page = 1;
  fetchPosts();
}

// 상세 페이지 이동
const goDetail = (id) => router.push(`/board/${id}`)

// 초기 로딩
onMounted(() => fetchPosts())
</script>