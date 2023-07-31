import {defineStore} from 'pinia'

export const useUserStore = defineStore('userStore', {
    state: () => {
        return {
            user: {},
            token: '',
            avatarUrl: ''
        }
    },
    getters: {
        gettersUser() {
            return this.user;
        }
    },
    actions: {}
})
