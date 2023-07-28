import {defineStore} from 'pinia'

export const useUserStore = defineStore('userStore', {
    state: () => {
        return {
            user: null,
            avatarSrc:'api/common/avatar/download'
        }
    },
    getters: {
        gettersUser() {
            return this.user;
        }
    },
    actions: {}
})
