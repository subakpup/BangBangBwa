<template>
  <div class="mt-12 pt-8 border-t border-gray-100">
    <h3 class="text-lg font-bold text-gray-800 mb-6 flex items-center gap-2">
      댓글 <span class="text-[#AE8B72]">{{ totalCommentCount }}</span>
    </h3>

    <div v-if="isLogin" class="mb-8 flex gap-3">
      <div class="flex-1">
        <textarea 
          v-model="mainContent"
          placeholder="따뜻한 댓글을 남겨주세요 :)"
          class="w-full p-4 bg-gray-50 rounded-xl border border-gray-200 focus:outline-none focus:border-[#AE8B72] focus:ring-1 focus:ring-[#AE8B72] resize-none transition-all h-24 text-sm"
          @keydown.enter.prevent="handleWrite(null)"
        ></textarea>
      </div>
      <button 
        @click="handleWrite(null)"
        class="h-24 px-6 rounded-xl bg-[#AE8B72] text-white font-medium hover:bg-[#96755E] transition-colors shrink-0"
      >
        등록
      </button>
    </div>
    
    <div v-else class="mb-8 p-6 bg-gray-50 rounded-xl text-center text-gray-500 text-sm">
      댓글을 작성하려면 <router-link to="/login" class="text-[#AE8B72] underline font-medium">로그인</router-link>이 필요합니다.
    </div>

    <div class="space-y-6">
      <div v-if="comments.length === 0" class="py-8 text-center text-gray-400 text-sm">
        아직 작성된 댓글이 없습니다. 첫 번째 댓글을 남겨보세요!
      </div>

      <div 
        v-for="comment in comments" 
        :key="comment.commentId" 
        class="border-b border-gray-50 pb-6 last:border-0"
      >
        <div class="group flex gap-4">
          <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center shrink-0">
             <span class="text-gray-400 text-xs font-bold">{{ comment.writerName ? comment.writerName.charAt(0) : '?' }}</span>
          </div>

          <div class="flex-1">
            <div class="flex items-center justify-between mb-1">
              <div class="flex items-center gap-2">
                <span class="font-bold text-gray-800 text-sm">{{ comment.writerName }}</span>
                <span class="text-xs text-gray-400">
                  {{ comment.createdAt ? comment.createdAt.replace('T', ' ').substring(0, 16) : '' }}
                </span>
              </div>
              
              <div class="flex gap-2 text-xs">
                <button v-if="isLogin" @click="toggleReply(comment.commentId)" class="text-gray-400 hover:text-[#AE8B72]">답글</button>
                <template v-if="isMyComment(comment.userId)">
                   <button @click="toggleEdit(comment)" class="text-gray-400 hover:text-blue-500">수정</button>
                   <button @click="handleDelete(comment.commentId)" class="text-gray-400 hover:text-red-500">삭제</button>
                </template>
              </div>
            </div>

            <div v-if="editingId === comment.commentId" class="mt-2">
                <textarea v-model="editContent" class="w-full p-3 border rounded-lg text-sm bg-white focus:outline-none focus:border-[#AE8B72]" rows="2"></textarea>
                <div class="flex justify-end gap-2 mt-2">
                    <button @click="editingId = null" class="text-xs text-gray-500">취소</button>
                    <button @click="handleUpdate(comment.commentId)" class="text-xs text-[#AE8B72] font-bold">저장</button>
                </div>
            </div>
            <p v-else class="text-gray-700 text-sm leading-relaxed whitespace-pre-wrap">{{ comment.content }}</p>
          </div>
        </div>

        <div v-if="replyingId === comment.commentId" class="mt-4 pl-14 flex gap-2">
            <textarea 
                v-model="replyContent" 
                placeholder="답글 내용을 입력하세요." 
                class="flex-1 p-3 border rounded-lg text-sm bg-gray-50 h-20 resize-none focus:outline-none focus:border-[#AE8B72]"
            ></textarea>
            <button @click="handleWrite(comment.commentId)" class="px-4 bg-[#AE8B72] text-white text-xs rounded-lg hover:bg-[#96755E]">등록</button>
        </div>

        <div 
            v-if="comment.children && comment.children.length > 0"
            class="mt-4 pl-14 space-y-4"
        >
            <div 
                v-for="reply in comment.children" 
                :key="reply.commentId" 
                class="flex gap-4 bg-gray-50/50 p-4 rounded-xl"
            >
                <div class="w-8 h-8 rounded-full bg-gray-50 flex items-center justify-center shrink-0 text-[10px]">
                    <span class="text-gray-400 font-bold">↳</span>
                </div>
                <div class="flex-1">
                    <div class="flex items-center justify-between mb-1">
                        <div class="flex items-center gap-2">
                            <span class="font-bold text-gray-700 text-xs">{{ reply.writerName }}</span>
                            <span class="text-[10px] text-gray-400">
                                {{ reply.createdAt ? reply.createdAt.replace('T', ' ').substring(0, 16) : '' }}
                            </span>
                        </div>
                        
                        <template v-if="isMyComment(reply.userId)">
                            <div class="flex gap-2 text-[10px]">
                                <button @click="toggleEdit(reply)" class="text-gray-400 hover:text-blue-500">수정</button>
                                <button @click="handleDelete(reply.commentId)" class="text-gray-400 hover:text-red-500">삭제</button>
                            </div>
                        </template>
                    </div>

                    <div v-if="editingId === reply.commentId" class="mt-2">
                        <textarea v-model="editContent" class="w-full p-2 border rounded-lg text-xs bg-white focus:outline-none focus:border-[#AE8B72]" rows="2"></textarea>
                        <div class="flex justify-end gap-2 mt-2">
                            <button @click="editingId = null" class="text-xs text-gray-500">취소</button>
                            <button @click="handleUpdate(reply.commentId)" class="text-xs text-[#AE8B72] font-bold">저장</button>
                        </div>
                    </div>
                    <p v-else class="text-gray-600 text-sm whitespace-pre-wrap">{{ reply.content }}</p>
                </div>
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { getCommentList, writeComment, removeComment, modifyComment } from '@/api/commentApi'; 
import { isLogin, userId } from '@/stores/auth'; 

const props = defineProps({
  postId: {
    type: [String, Number],
    required: true
  }
});

const comments = ref([]); // 여기엔 이제 트리 구조가 담깁니다.

const mainContent = ref(''); 
const replyContent = ref(''); 
const editContent = ref(''); 

const replyingId = ref(null); 
const editingId = ref(null);  

const currentUserId = computed(() => userId.value);

const isMyComment = (writerId) => {
    return isLogin.value && String(currentUserId.value) === String(writerId);
};

const totalCommentCount = computed(() => {
  return comments.value.reduce((total, comment) => {
    // 부모 댓글 1개 + 해당 댓글의 대댓글(children) 개수
    const childCount = comment.children ? comment.children.length : 0;
    return total + 1 + childCount;
  }, 0);
});

// 목록 조회
const loadComments = async () => {
  const res = await getCommentList(props.postId);
  if (res && res.success === "SUCCESS") {
    // 백엔드가 이미 트리를 만들어서 주므로 필터링 불필요
    comments.value = res.data || [];
  }
};

const handleWrite = async (parentId) => {
  const contentToWrite = parentId ? replyContent.value : mainContent.value;

  if (!contentToWrite.trim()) {
    alert("내용을 입력해주세요.");
    return;
  }

  const res = await writeComment(props.postId, contentToWrite, parentId);
  
  if (res && res.success === "SUCCESS") {
    if (parentId) {
        replyContent.value = '';
        replyingId.value = null; 
    } else {
        mainContent.value = '';
    }
    await loadComments(); 
  } else {
    alert("댓글 등록 실패");
  }
};

const toggleReply = (commentId) => {
    if (replyingId.value === commentId) {
        replyingId.value = null; 
    } else {
        replyingId.value = commentId; 
        replyContent.value = ''; 
        editingId.value = null; 
    }
};

const toggleEdit = (comment) => {
    editingId.value = comment.commentId;
    editContent.value = comment.content; 
    replyingId.value = null; 
};

const handleUpdate = async (commentId) => {
    if (!editContent.value.trim()) {
        alert("내용을 입력해주세요.");
        return;
    }

    const res = await modifyComment(commentId, editContent.value);
    if (res && res.success === "SUCCESS") {
        editingId.value = null; 
        await loadComments();
    } else {
        alert("수정 실패");
    }
};

const handleDelete = async (commentId) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  const res = await removeComment(commentId);
  if (res && res.success === "SUCCESS") {
    await loadComments();
  } else {
    alert("삭제 실패");
  }
};

onMounted(() => {
  loadComments();
});
</script>