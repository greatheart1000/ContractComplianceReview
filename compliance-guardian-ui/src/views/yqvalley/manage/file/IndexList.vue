<template>
  <div class="fileList">
    <!-- 查询条件 -->
    <div class="search-box-one" style="width:100%;overflow:visible;">
      <div style="width:33%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">操作日期：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-date-picker
              v-model="queryTime"
              type="daterange"
              style="width:100%;"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :disabled-date="disabledDate"
              :shortcuts="shortcuts"
              size="default"
            >
            </el-date-picker>
          </div>
        </div>
      </div>
      <div style="width:33%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">文件名称：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-input v-model="queryData.fileName" placeholder="请输入文件名称" clearable style="width:100%;"></el-input>
          </div>
        </div>
      </div>
      <div style="width:33%;float:left;margin-top:20px;">
        <div class="search-box-right" style="overflow: visible;width: 100%;">
          <div class="query" @click="empty" style="margin-right: 20px;">
            <font-awesome-icon icon="fas fa-refresh" />重置
          </div>
          <div class="query" @click="search" style="margin-right: 10px;">
            <font-awesome-icon icon="fas fa-search" />查询
          </div>
          <div class="more-down" @click="moreCondition = !moreCondition" v-if="!moreCondition">
            <span v-text="'展开'"></span>
            <font-awesome-icon icon="fas fa-angle-double-down" />
          </div>
          <div class="more-down" @click="moreCondition = !moreCondition" v-else>
            <span v-text="'收起'"></span>
            <font-awesome-icon icon="fas fa-angle-double-up" />
          </div>
        </div>
      </div>
    </div>
    <div v-if="moreCondition" class="search-box-two" style="width: 100%; height: 52px; float: left; margin-top: -10px;">
      <div style="width:33%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">访问权限：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-select
                v-model="queryData.accessAuthority"
                placeholder="请选择访问权限"
                style="width:100%"
                clearable
              >
                <el-option
                  v-for="item in accessAuthorityList"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemCode"
                >
                </el-option>
              </el-select>
          </div>
        </div>
      </div>
      <div style="width:33%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">文件类型：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-select
                v-model="queryData.fileType"
                placeholder="请选择文件类型"
                style="width:100%"
                clearable
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('WALLEY_FILE_TYPE')"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemValue"
                >
                </el-option>
              </el-select>
          </div>
        </div>
      </div>
    </div>
    <div class="clear"></div>
    <!-- 操作按钮 -->
    <div class="btn-box" style="margin-top: 10px;">
      <el-upload 
        class="upload-demo" 
        style="width:60px;float:left;margin-right: 6px;" 
        :action="$yqvutil.getContextPath()+'yqvservice/file/upload?fileType=public&authority=public'"
        :headers="yqvHeaders" 
        :on-success="callbackUploadSuccess" 
        :on-error="callbackUploadError" 
        multiple 
        :limit="1" 
        :accept="fileTypeConf">
          <el-button size="small" type="primary" style="width:60px;">
            <font-awesome-icon icon="fas fa-upload" style="margin-right:5px;margin-left: -6px;" />上传
          </el-button >
        </el-upload>
      <div class="start" style="width:60px;background: #f56c6c;" @click="deleteFileList">
        <font-awesome-icon icon="fas fa-trash-alt" />删除
      </div>
    </div>
    <!--表格-->
    <div class="tableBox">
      <el-table
        :data="tableData"
        border
        ref="table"
        @selection-change="checkAll"
        style="width: 100%"
        tooltip-effect="light"
        :height="moreCondition?listHeight-110:listHeight-68"
      >
        <el-table-column type="selection" width="55" align="center">
        </el-table-column>
        <el-table-column fixed="left" label="操作" width="50" align="center">
          <template #default="scope">
            <font-awesome-icon v-if="scope.row.fileId && scope.row.fileId!=null && scope.row.fileId!=''"
              icon="fas fa-download"
              style="float: left;color:#597ef7;font-size:18px;width: 32px;cursor: pointer;"
              @click="download(scope.row)"
              title="下载文件"
            />
          </template>
        </el-table-column>
        <el-table-column label="访问权限" align="center" width="80" :show-overflow-tooltip="true">
          <template #default="scope">
            <span v-text="setAccessAuthority(scope.row.accessAuthority)"></span>
          </template>
        </el-table-column>
        <el-table-column label="文件类型" align="center" width="100" :show-overflow-tooltip="true">
          <template #default="scope">
            <span v-text="$yqvutil.getSelectItemName('WALLEY_FILE_TYPE',scope.row.fileType)"></span>
          </template>
        </el-table-column>
        <el-table-column label="文件名称" align="left" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.fileName"></div>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" align="center" width="80">
          <template #default="scope">
            <div v-text="$yqvutil.getFileSize(scope.row.fileSize)"></div>
          </template>
        </el-table-column>
        <el-table-column label="下载次数" align="center" width="80">
          <template #default="scope">
            <div v-text="scope.row.downloadNum"></div>
          </template>
        </el-table-column>
        <el-table-column label="上传用户" align="left" width="110" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.createName"></div>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="140" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="$yqvutil.setFormTime(scope.row.createDate, 1)"></div>
          </template>
        </el-table-column>
      </el-table>
      <div class="paging-box">
        <div class="block">
          <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totals"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
export default {
  name: "FileManageList",
  data() {
    return {
      moreCondition:false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //列表数据
      tableData: [],
      //全选单选容器
      container: [],
      //文件类型
      fileTypeConf:"*",
      uploadUrl:this.$yqvutil.getContextPath()+"yqvservice/file/upload?fileType=public&authority=public",
      //查询条件
      queryData:{
        accessAuthority:"",
        fileType:"",
        fileName:"",
        beginTime: new Date(),
        endTime: new Date(),
      },
      accessAuthorityList: [{"itemCode":"public","itemName":"公有"},{"itemCode":"private","itemName":"私有"}],
      queryTime: [new Date(),new Date()],
      disabledDate(time) {
        return time.getTime() > Date.now();
      },
      shortcuts: [
          {
            text: '当月',
            value: () => {
              let date = new Date()
              date.setDate(1)
              let year= date.getFullYear()
              let month = date.getMonth()  + 1
              month  = month > 10 ? month :'0' + month
              let day  = date.getDate()
              const start = new Date(year +'-'+ month +'-'+ day)
              const end = new Date()
              return [start, end]
            },
          },
          {
            text: '当年',
            value: () => {
              const end = new Date()
              const start = new Date(new Date().getFullYear(), 0)
              return [start, end]
            },
          },{
            text: '近七天',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setDate(start.getDay() - 7)
              return [start, end]
            },
          },
          {
            text: '近一月',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 1)
              return [start, end]
            },
          },
          {
            text: '近三月',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 3)
              return [start, end]
            },
          },
          {
            text: '近六月',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 6)
              return [start, end]
            },
          },
        ],
    };
  },
  computed: {
    ...mapState(["listHeight", "yqvSelect"]),
    yqvHeaders() {
      return {
        Authorization: this.$yqvutil.getToken()
      };
    }
  },
  mounted() {
    this.fileTypeConf = this.$yqvutil.getAppConfigValue("file_storage_limit_suffix");
    this.getFileList();
  },
  components: {
    
  },
  watch: {

  },
  methods: {
    /********************************查询缓存列表*****************************/
    search() {
      this.getFileList(true);
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.queryData.fileType = "";
      this.queryData.accessAuthority = "";
      this.queryData.fileName = "";
      this.queryData.beginTime = new Date();
      this.queryData.endTime = new Date();
      this.queryTime = [new Date(),new Date()],
      this.getFileList(true);
    },
    /********************************全选*****************************/
    checkAll(val) {
      //进行赋值,判断是否选中内容
      this.container = val;
    },
    /********************************渲染访问权限*****************************/
    setAccessAuthority(val) {
      if (val == 'public') {
        return "公有";
      }else if(val == 'private') {
        return "私有";
      }else{
        return val;
      }
    },
    /********************************删除文件*****************************/
    deleteFileList(row) {
      if (this.container.length != 0) {
        this.$confirm("此操作将永久删除文件, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          closeOnClickModal: false
        })
          .then(() => {
            let vm = this;
            let fileList = [];
            for (let i = 0; i < this.container.length; i++) {
              fileList.push(this.container[i].fileId);
            }
            let data = {
              fileIds: fileList
            };
            this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/file/delete`, data)
              .then(function(response) {
                vm.$message({
                  type: "success",
                  message: "删除成功!"
                });
                vm.getFileList(true);
              })
              .catch(function(error) {});
          })
          .catch(() => {});
      } else {
        this.$message({
          showClose: true,
          message: "请选择文件",
          type: "warning"
        });
      }
    },
    /********************************上传成功*****************************/
    callbackUploadSuccess(res, file) {
      this.$message({
        type: "success",
        message: "上传成功!"
      });
      this.getFileList(true);
    },
    /********************************上传失败*****************************/
    callbackUploadError(err, file, fileList) {
      this.$message.error(JSON.parse(err.message).message);
    },
    /********************************下载文件*****************************/
    download(val) {
      let src = this.$yqvutil.getContextPath()+"fileservice/file/download?fileId=" + val.fileId;
      this.$yqvutil.downloads(src);
    },
    /********************************查询文件列表*****************************/
    getFileList(flag) {
      let vm = this;
      let data = this.queryData;
      if(vm.queryTime && vm.queryTime.length>1){
        data.beginTime = vm.queryTime[0];
        data.endTime = vm.queryTime[1];
      }else{
        data.beginTime = null;
        data.endTime = null;
      }
      if (flag) {
        vm.currentPage = 1;
        vm.pageSize = 10;
        vm.totals = 0;
      }
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/file/page?currentPage=${vm.currentPage}&pageSize=${
            vm.pageSize
          }`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.tableData = response.data.result;
            vm.totals = response.data.page.totalRows;
          }
        })
        .catch(function(error) {});
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getFileList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getFileList();
    }
  }
};
</script>

<style scoped lang="less">
.queryList ::v-deep(.el-form-item__label) {
  width: 90px !important;
  height: 35px;
}
.upload-demo ::v-deep(.el-upload-list) {
  display: none !important;
}
.upload-demo ::v-deep(.el-button) {
  width: 72px;
  height: 28px;
  line-height: 8px;
  background: #597ef7;
  padding: 6px 15px;
  font-size: 14px;
  border-radius: 2px;
  border: none;
}
.fileList {
  .more-down {
    width: 60px;
    float: left;
    color: #5675f5;
    height: 32px;
    line-height: 32px;
    text-align: center;
    cursor: pointer;
  }
  height: calc(100% - 98px);
  .queryList {
    margin-left: 24px;
    .el-form {
      display: flex;
      flex-wrap: nowrap;
      .el-form-item {
        flex: 1;
        display: flex;
        flex-wrap: nowrap;
      }
      .queryBtn {
        flex: 0 0 130px;
      }
    }
  }
  .btnList {
    margin-left: 24px;
    margin-bottom: 14px;
    padding-top: 18px;
    box-sizing: border-box;
    display: flex;
    height: 48px;
    span {
      line-height: 30px;
      margin-left: 14px;
    }
    .el-input {
      width: 300px;
      margin-right: 14px;
    }
  }
  .authorityBox {
    height: 100%;
    position: relative;
  }
  .abox-lableName {
    width: 76px;
    display: inline-block;
    span {
      color: red;
      font-size: 14px;
    }
  }
  .el-input {
    width: 220px;
  }
  .el-select {
    width: 220px;
  }
  .span-white{
    color: white!important;
  }
  .span-red{
    color: red;
  }
}
</style>
