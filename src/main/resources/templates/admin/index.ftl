<#include "./layout/layout.ftl"/>
<@html page_title="首页" page_tab="index">
  <section class="content-header">
    <h1>
      首页
      <small>仪表盘</small>
    </h1>
    <ol class="breadcrumb">
      <li><a href="/admin/index"><i class="fa fa-dashboard"></i> 首页</a></li>
      <li class="active">仪表盘</li>
    </ol>
  </section>
  <section class="content">
    <div class="row">
      <div class="col-lg-6">
        <div class="box box-info">
          <div class="box-header with-border">
            <h3 class="box-title">系统状态</h3>

            <div class="box-tools pull-right">
              <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
            </div>
          </div>
          <!-- /.box-header -->
          <#--<div class="box-body">-->
            <div class="table-responsive">
              <table class="table no-margin">
                <tbody>
                <tr>
                  <th width="140">内存</th>
                  <td>
                    <div class="progress">
                      <div class="progress-bar progress-bar-info progress-bar-striped" style="width: ${usedMemory * 100 / totalMemorySize}%">
                        ${usedMemory/1024/1024}GB/${totalMemorySize/1024/1024}GB
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th>系统</th>
                  <td>${os_name}</td>
                </tr>
                <tr>
                  <th>CPU使用率</th>
                  <td>${(systemCpuLoad * 100)?string('#.##')}%</td>
                </tr>
                <tr>
                  <th>JVM CPU使用率</th>
                  <td>${(processCpuLoad * 100)?string('#.##')}%</td>
                </tr>
                </tbody>
              </table>
            <#--</div>-->
            <!-- /.table-responsive -->
          </div>
        </div>
      </div>
    </div>
  </section>
</@html>
