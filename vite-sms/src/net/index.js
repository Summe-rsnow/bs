import axios from 'axios';

axios.defaults.baseURL = '/api';
axios.defaults.withCredentials = true;

const defaultFailure = (msg) => console.log(msg);
const defaultError = (error) => console.log(error);

function post(url, data, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data)
        .then(({data}) => {
            if (data.code === 1)
                success(data.data, data.msg)
            else
                failure(data.msg)
        }).catch(error)
}

function get(url, success, failure = defaultFailure, error = defaultError) {
    axios.get(url)
        .then(({data}) => {
            if (data.code === 1)
                success(data.data, data.msg)
            else
                failure(data.msg)
        }).catch(error)
}

function del(url, success, failure = defaultFailure, error = defaultError) {
    axios.delete(url)
        .then(({data}) => {
            if (data.code === 1)
                success(data.data, data.msg)
            else
                failure(data.msg)
        }).catch(error)
}

function avatar(data, success, failure = defaultFailure, error = defaultError) {
    axios.post('/common/avatar/upload', data, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
        .then(({data}) => {
            if (data.code === 1)
                success(data.data, data.msg)
            else
                failure(data.msg)
        }).catch(error)
}

export {get, post, del, avatar}