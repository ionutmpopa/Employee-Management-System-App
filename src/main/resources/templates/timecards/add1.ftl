[#ftl]
[#import "/spring.ftl" as spring /]
<html lang="en">
<head>
<script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
<script>
      $(document).ready(function() {
              var selectedProject = 0;
               var selectedEmployee = 0;

               $('#inputGroupSelect01').on('change',function() {
                                selectedEmployee = $(this).find("option:selected").val();
                            });

              $('#inputGroupSelect02').on('change',function() {
                  selectedProject = $(this).find("option:selected").val();

              });

              $("#myform").on("submit", function(){
                var selectedProjectWithoutCommas = selectedProject.replace(/,/g, '');
                  var selectedEmployeeWithoutCommas = selectedEmployee.replace(/,/g, '');

                 document.getElementById('project_id').value = selectedProjectWithoutCommas;
                 document.getElementById('employee_id').value = selectedEmployeeWithoutCommas;
                 return true;
               })
      });
 </script>
[#include '/bootstrap_header.ftl']
</head>
<body>

<div class="container">
    <a href="/"> <img src="[@spring.url '/images/logo.png' /]" width="100"/>
    </a>

    <ol class="breadcrumb">
    <li class="active"><a href="/">Home</a></li>
                <li><a href="/employee">Employees</a></li>
                                    <li><a href="/project">Projects</a></li>
                                    <li><a href="/cost">Costs</a></li>
                                    <li><a href="/timecards">Timecards</a></li>
                                    <li><a href="/logout">Logout</a>
    		</ol>


<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Edit/Add Timecard</h3>
    </div>
    <div class="panel-body">

[#if errors??]
    [#list errors as error]
       <span style="color:red"> ${error}</span>
    <br>
    [/#list]
[/#if]
<form method="post" action="/timecards/save" id="myform">
    Date: <input name="date" type="input" value="[#if timecard.date??]${timecard.date?string('dd/MM/yyyy')}[/#if]">
    <br>
   Hours: <input name="hours" type="input"  value="${timecard.hours!''}">
    <br>


       <div class="input-group mb-3">
          <div class="input-group-prepend">
            <label class="input-group-text" for="inputGroupSelect01">Employee</label>

          <select class="custom-select" id="inputGroupSelect01">
          <option selected>Choose...</option>
          [#if employees??]
           [#list employees as employee]
                         <option value="${employee.id!''}">${employee.firstName!''}</option>
                  [/#list]
                  [/#if]

          </select>
        </div>
        </div>
 <input id ="employee_id" name="employee_id" type="hidden" value="${timecard.employee_id!''}"/>
        <br>


    <div class="input-group mb-3">
      <div class="input-group-prepend">
        <label class="input-group-text" for="inputGroupSelect02">Project</label>

      <select class="custom-select" id="inputGroupSelect02">
      <option selected>Choose...</option>
      [#if projects??]
       [#list projects as project]
                     <option value="${project.id!''}">${project.name!''}</option>
              [/#list]
              [/#if]

      </select>
      </div>
    </div>

    <input id ="project_id" name="project_id" type="hidden" value="${timecard.project_id!''}"/>
    <br>

    Comment: <input name="comment" type="input"  value="${timecard.comment!''}">
    <br>

    [#if timecard.id??]
        <input name="id" type="hidden" value="${timecard.id?c}"/>
    [/#if]
    <input value="save" type="submit"/>
</form>

   </div>
 </div>
 </div>
 [#include '/bootstrap_footer.ftl']
 </body>
 </html>




