<template>
    <div class="modal">
      <div class="modal-content">
        <button class="close" @click="$emit('close')">×</button>
        <slot name="header"></slot>
        <slot name="body"></slot>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'Modal',
  };
  </script>
  
  <style scoped>
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .modal-content {
    background: white;
    padding: 20px;
    border-radius: 5px;
    position: relative;
  }
  .close {
    position: absolute;
    top: 10px;
    right: 10px;
    border: none;
    background: none;
    font-size: 20px;
  }
  </style>