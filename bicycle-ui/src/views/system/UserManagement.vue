<template>
  <div class="user-management">
    <!-- 标题和添加用户按钮 -->
    <div class="header-actions">
      <h2>用户管理</h2>
      <el-button type="primary" @click="dialogVisible = true">添加用户</el-button>
    </div>

    <!-- 用户列表表格 -->
    <el-table :data="users" style="width: 100%" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="username" label="用户名" align="center" />
      <el-table-column prop="nickname" label="昵称" align="center" />
      <el-table-column prop="email" label="邮箱" align="center" />
      <el-table-column prop="mobile" label="手机号" align="center" />
      <el-table-column prop="sex" label="性别" align="center">
        <template #default="scope">
          {{ scope.row.sex === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
            {{ scope.row.status === 0 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm
            title="确定删除该用户吗？"
            @confirm="deleteUser(scope.row.id)"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门">
            <el-option label="研发部" :value="103"></el-option>
            <el-option label="市场部" :value="104"></el-option>
            <el-option label="测试部" :value="105"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="岗位" prop="postIds">
          <el-select v-model="form.postIds" multiple placeholder="请选择岗位">
            <el-option label="开发" :value="1"></el-option>
            <el-option label="测试" :value="2"></el-option>
            <el-option label="运维" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="role_admin"></el-option>
            <el-option label="人事" value="role_hr"></el-option>
            <el-option label="销售经理" value="role_manager"></el-option>
            <el-option label="销售人员" value="role_sale"></el-option>
            <el-option label="库存管理" value="role_stock"></el-option>
            <el-option label="财务" value="role_finance"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      users: [],
      dialogVisible: false,
      isEdit: false,
      form: {
        id: null,
        username: '',
        password: '',
        nickname: '',
        email: '',
        mobile: '',
        sex: 1,
        deptId: null,
        postIds: [],
        status: 0,
        role: '',
        remark: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        mobile: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        deptId: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      }
    };
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get('/api/users');
        console.log('API Response:', response.data);
        this.users = response.data.data;
        console.log('Users after assignment:', this.users);
      } catch (error) {
        console.error('Error fetching users:', error);
        ElMessage.error('获取用户列表失败');
      }
    },
    handleEdit(user) {
      console.log('Editing user:', user);
      this.isEdit = true;
      const userData = { ...user };
      if (typeof userData.postIds === 'string' && userData.postIds) {
        userData.postIds = userData.postIds.split(',').map(Number);
      } else {
        userData.postIds = [];
      }
      this.form = userData;
      this.dialogVisible = true;
    },
    resetForm() {
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
      this.form = {
        id: null,
        username: '',
        password: '',
        nickname: '',
        email: '',
        mobile: '',
        sex: 1,
        deptId: null,
        postIds: [],
        status: 0,
        role: '',
        remark: ''
      };
      this.isEdit = false;
      this.dialogVisible = false;
    },
    async handleSubmit() {
      try {
        // 表单验证
        await this.$refs.form.validate();
        
        // 处理表单数据
        const formData = { ...this.form };
        
        // 处理 postIds，将数组转换为字符串
        if (Array.isArray(formData.postIds)) {
          formData.postIds = formData.postIds.join(',');
        }

        if (this.isEdit) {
          await axios.put(`/api/users/${formData.id}`, formData);
          ElMessage.success('修改成功');
        } else {
          await axios.post('/api/users', formData);
          ElMessage.success('添加成功');
        }
        this.fetchUsers();
        this.resetForm();
      } catch (error) {
        console.error('提交失败:', error);
        ElMessage.error(this.isEdit ? '修改失败' : '添加失败');
      }
    },
    async deleteUser(id) {
      console.log('Deleting user with ID:', id);
      if (!id) {
        ElMessage.error('用户ID不能为空');
        return;
      }
      try {
        await axios.delete(`/api/users/${id}`);
        ElMessage.success('删除成功');
        this.fetchUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
        ElMessage.error('删除失败');
      }
    }
  }
};
</script>

<style scoped>
.user-management {
  padding: 20px;
  height: calc(100vh - 64px); /* 计算剩余高度，header 高度为 64px */
  overflow-y: auto; /* 允许内容区域滚动 */
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h2 {
  margin: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-table) {
  margin-top: 20px;
}
</style> 