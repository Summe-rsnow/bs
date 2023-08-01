import {createRouter, createWebHistory} from 'vue-router';
import {useUserStore} from "../stores/index.js";

const routerHistory = createWebHistory();

const routes = [
    {
        path: '/',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/home',
        component: () => import('../views/Home.vue'),
        children: [
            {
                path: '/info',
                component: () => import('../views/UserInfo.vue')
            },
            {
                path: '/select_grade',
                component: () => import('../views/SelectGrade.vue')
            },
            {
                path: '/grade_manager',
                component: () => import('../views/GradeManager.vue')
            },
            {
                path: '/user_manager',
                component: () => import('../views/UserManager.vue')
            },
            {
                path: '/course_manager',
                component: () => import('../views/CourseManager.vue')
            },
            {
                path: '/select_student',
                component: () => import('../views/SelectStudent.vue')
            },
            {
                path: '/select_course',
                component: () => import('../views/SelectCourse.vue')
            }
        ]
    },

]

const router = new createRouter({
    history: routerHistory,
    routes
});

router.beforeEach((to, from) => {
    const userStore = useUserStore();
    if (userStore.token) {
        return true;
    } else {
        if (to.path === '/') {
            return true;
        } else {
            return {path: '/'};
        }
    }
});
export default router;