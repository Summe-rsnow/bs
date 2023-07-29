import axios from 'axios';
import {useUserStore} from '../stores/index.js';

axios.defaults.baseURL = '/api';
axios.defaults.withCredentials = true;

const userStore = useUserStore();
const defaultFailure = (msg) => console.log(msg);
const defaultError = (error) => console.log(error);

function getToken() {
    return userStore.token;
}

axios.interceptors.request.use(
    (config) => {
        // 在 GET 请求中添加请求头
        if (getToken()) {
            // 添加通用的请求头
            config.headers['Authorization'] = getToken();
        }
        return config;
    },
    (error) => Promise.reject(error)
);

//所有请求底层都为post
function post(url, data, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data)
        .then(({data}) => {
            if (data.code === 1) success(data.data, data.msg);
            else failure(data.msg);
        })
        .catch(error);
}

function get(url, success, failure = defaultFailure, error = defaultError) {
    axios.post(url)
        .then(({data}) => {
            if (data.code === 1) success(data.data, data.msg);
            else failure(data.msg);
        })
        .catch(error);
}

function del(url, success, failure = defaultFailure, error = defaultError) {
    axios.post(url)
        .then(({data}) => {
            if (data.code === 1) success(data.data, data.msg);
            else failure(data.msg);
        })
        .catch(error);
}

function uploadAvatar(data, success, failure = defaultFailure, error = defaultError) {
    axios.post('/common/avatar/upload', data, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    })
        .then(({data}) => {
            if (data.code === 1) success(data.data, data.msg);
            else failure(data.msg);
        })
        .catch(error);
}

function downloadAvatar() {
    axios.post('common/avatar/download/' + userStore.user.id, {}, {responseType: 'blob'})
        .then((res) => {
            //设置src属性
            userStore.avatarUrl = URL.createObjectURL(res.data);
        })
        .catch(defaultError);
}

export {get, post, del, uploadAvatar, downloadAvatar};