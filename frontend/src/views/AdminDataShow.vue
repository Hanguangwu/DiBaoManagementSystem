<template>
  <div class="card" style="margin: 5px;">
    <h1>管理员数据展示</h1>
    <el-input v-model="data.username" placeholder="请输入内容" :prefix-icon="Search" clearable></el-input>
    <el-button type="primary" @click="loadData" style="margin-top: 10px;">查询</el-button>
    <el-button type="primary" @click="resetData" style="margin-top: 10px;">重置</el-button>
  </div>
  <div class="card" style="margin: 5px;">
    <el-button type="primary" @click="handleAdd">
      <Edit />新增用户
    </el-button>
    <el-button type="danger" @click="handleBatchDelete">
      <Delete />批量删除
    </el-button>
  </div>
  <div class="card" style="margin: 5px;">
    <el-table :data="data.tableData" border style="width: 100%" highlight-current-row
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="avatar" label="头像">
        <template #default="scope">
          <img v-if="scope.row.avatar" :src="scope.row.avatar"
            style="display:block; width: 40px; height: 40px; border-radius: 50%;" />
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column prop="occupation" label="职业" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button circle type="primary" :icon="Edit" @click="handleEdit(scope.row)"></el-button>
          <el-button circle type="danger" :icon="Delete" @click="handleDelete(scope.row.uid)"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="loadData" @prev-click="loadData" @next-click="loadData" @current-change="loadData"
      v-model:current-page="data.pageNum" v-model:page-size="data.pageSize" :page-sizes="[5, 10, 15, 20]" background
      layout="total, sizes, prev, pager, next, jumper" :total=data.total />
  </div>
  <div>
    <el-dialog v-model="data.formVisible" title="用户信息" width="500px" destroy-on-close>
      <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="80px" style="padding-right: 40px; padding-top:20px">
        <el-form-item label="用户名" prop="username" placeholder="请输入名称">
          <el-input :disabled="data.form.uid" v-model="data.form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload action="http://localhost:8080/files/upload" list-type="picture" :headers="{'token': data.user.token}"
            :on-success="handleAvatarSuccess">
            <el-icon type="primary">上传头像</el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname" placeholder="请输入昵称">
          <el-input v-model="data.form.nickname" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="data.form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
          <!-- <el-input-number style="width: 180px;" :min="18" :max="70" v-model="data.form.age" autocomplete="off"
            placeholder="请输入年龄" /> -->
        </el-form-item>
        <!-- <el-form-item label="地位">
          <el-input v-model="data.form.status" autocomplete="off" placeholder="请输入地位" />
        </el-form-item> -->
        <el-form-item label="职业" prop="occupation" >
          <el-input :rows="3" type="textarea" v-model="data.form.occupation" autocomplete="off" placeholder="请输入职业" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { Search, Edit, Delete } from '@element-plus/icons-vue';
import service from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

const formRef = ref(null)

const data = reactive({
  user: JSON.parse(localStorage.getItem('grantedUser') || '{}'),
  tableData: [],
  username: null,
  dialogVisible: false,
  formVisible: false,
  pageNum: 1,
  pageSize: 5,
  total: 0,
  form: null,
  ids: [],
  rules: {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      // { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
    ],
    nickname: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      // { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
    ],
    occupation: [
      { required: true, message: '请输入职业', trigger: 'blur' },
      // { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
    ]
  }
})


const loadData = () => {
  service.get('/admin/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      username: data.username
    }
  }).then(res => {
    data.tableData = res.data.list
    data.total = res.data.total
  })
}
loadData()

const resetData = () => {
  data.name = null
  loadData()
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))  //深拷贝，防止修改原始数据
  data.formVisible = true
}

const handleAdd = () => {
  data.formVisible = true
  data.form = {}
}

const handleAvatarSuccess = (res) => {
    data.form.avatar = res.data
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该条数据吗？', '确认删除', { type: 'warning' }).then(() => {
    service.delete('/admin/delete/' + id).then((res) => {
      if (res.code === "200") {
        ElMessage.success('删除成功')
        data.formVisible = false
        loadData()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

const handleSelectionChange = (rows) => {
  // console.log(rows)
  data.ids = rows.map(item => item.uid)
}

const handleBatchDelete = () => {
  if (data.ids.length === 0) {
    ElMessage.error('请先选择要删除的数据')
    return
  }
  ElMessageBox.confirm('确认批量删除选中数据吗？', '确认批量删除', { type: 'warning' }).then(() => {

    //取出选中行的id组成数组
    service.delete('/admin/batchDelete', { data: data.ids }).then((res) => {
      if (res.code === "200") {
        ElMessage.success('批量删除成功')
        data.formVisible = false
        loadData()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

const handleSave = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (data.form.uid) {
        update()
      } else {
        add()
        //ElMessage.error('只能修改，不能新增')
      }
    } else {
      return false
    }
  })
}

const add = () => { //新增的对象没有id
  service.post('/admin/insert', data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success('新增成功')
      data.formVisible = false
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  })
}
const update = () => { //修改的对象有id

  service.put('/admin/update', data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success('修改成功')
      data.formVisible = false
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

</script>

<style scoped></style>
