<template>

<div class="post-container">
    <div class="post-header">
        <div class="flex items-center gap-2 mb-3">
            <span class="post-badge">
            <MapPin :size="12" class="inline mb-0.5 mr-1"/>
            {{ post.sidoNm }} {{ post.sggNm }}
            </span>
            <span class="text-xs text-gray-400">
            {{ post.createdDate ? post.createdDate.replace('T', ' ') : '' }}
            </span>
        </div>
        
        <h1 class="post-title-text">{{ post.title }}</h1>
        
        <div class="flex items-center justify-between mt-4">
        <div class="post-meta">
            <span class="flex items-center gap-1">
            <User :size="14"/> {{ post.name }}
            </span>
            <span v-if="post.viewCnt" class="flex items-center gap-1">
            <Eye :size="14"/> {{ post.viewCnt }}
            </span>
        </div>
        
        <div v-if="isMyPost" class="flex gap-2">
            <button @click="router.push(`/board/edit/${postId}`)" class="text-sm text-gray-500 hover:text-[#AE8B72] underline">
            수정
            </button>
            <button @click="handleDelete" class="text-sm text-gray-400 hover:text-red-500 underline">
            삭제
            </button>
        </div>
        </div>
    </div>

    <div class="post-content">
        {{ post.content }}
    </div>

    <div class="mt-10 pt-6 border-t border-gray-100 flex justify-center">
        <button @click="router.push('/board')" class="btn-base bg-white text-[#AE8B72] border border-[#AE8B72] hover:bg-gray-50">
        목록으로
        </button>
    </div>

</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostDetail, removePost } from '@/api/boardApi'
import { userId } from '@/stores/auth'
import { User, Eye, MapPin } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const postId = route.params.id
const post = ref({})
const isMyPost = ref(false)

onMounted(async () => {
  const res = await getPostDetail(postId);
  if (res && res.data) {
    post.value = res.data;

    console.log(post.value);
    
    // [중요] 내 글인지 확인 (스토어의 userId와 게시글의 userId 비교)
    // 백엔드 PostDetailDto에 userId가 포함되어 있어야 합니다.
    // authStore.userInfo가 없다면 로그인 안 한 상태
    if (post.value.userId === userId.value) {
        isMyPost.value = true;
    }
  }
})

const handleDelete = async () => {
  if(!confirm("정말 삭제하시겠습니까?")) return;
  
  const res = await removePost(postId);
  // 204 No Content는 res.data가 없을 수 있음
  if (res && (res.status === 200 || res.success === "SUCCESS")) {
    alert("삭제되었습니다.");
    router.push('/board');
  }
}
</script>