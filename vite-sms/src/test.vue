<template>
  <form ref="myForm">
    <input accept="image/*" type="file" @change="onChange"/>
    <input type="button" value="提交" @click="onSubmit($event)"/>
  </form>

  <img
      v-if="previewImage"
      :src="previewImage"
      alt="预览图"
      style="max-width: 100%; max-height: 100%"
  />

  <Cropper
      v-if="cropperVisible"
      :imagePath="imagePath"
      fileType="blob"
      @cancel="onCancel"
      @save="onSave"
  />
</template>
js:

<script>
import axios from "axios";
import {reactive, ref, toRefs} from "vue";
import Cropper from "vue3-cropper";
import "vue3-cropper/lib/vue3-cropper.css";

const URL = window.URL || window.webkitURL;
export default {
  components: {
    Cropper
  },
  setup() {
    // cropper状态
    const state = reactive({
      cropperVisible: false,
      imagePath: "",
      previewImage: null,
    });
    // 获取图片
    const onChange = (e) => {
      const file = e.target.files[0];
      state.imagePath = URL.createObjectURL(file);
      state.cropperVisible = true;
    };
    // 保存选取的图片
    const onSave = (res) => {
      if (typeof res === "string") {
        state.previewImage = res;
      } else {
        state.previewImage = URL.createObjectURL(res);
      }
      state.cropperVisible = false;
    };
    // 取消选取
    const onCancel = () => {
      state.cropperVisible = false;
    };
    // 提交表单——上传文件
    const myForm = ref(null);
    const onSubmit = (e) => {
      let newFile = new File([state.file], state.file.name, {
        type: state.file.type,
      });
      let formData = new FormData();
      formData.append("file", newFile);
      formData.append("enctype", "multipart/form-data");
      console.log("提交头像");
      axios({
        method: "post",
        // 提交地址
        url: "",
        data: formData,
      }).then(
          (response) => {
            console.log("上传头像返回信息", response.data.data);
          },
          (error) => {
            console.log("上传头像请求失败", error.data.message);
          }
      );
      // 关闭提交表单
      e.preventDefault();
    };
    return {
      ...toRefs(state),
      onChange,
      onSave,
      onCancel,
      myForm,
      onSubmit,
    };
  },
};
</script>