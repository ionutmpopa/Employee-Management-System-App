[#ftl]
[#import "/spring.ftl" as spring /]
<html lang="en">
<head>
  [#include '/bootstrap_header.ftl']
</head>
<body>

<div class="container">
    <a href="/"> <img src="[@spring.url '/images/logo.png' /]" width="100"/>
    </a>

   [#if user??] <div style="float: right"><b>Hello: ${user}</b></div>
      [/#if]



      <ol class="breadcrumb">
      			<li><a href="/">Home</a></li>
      			[#if user??]
                          <li><a href="/employee">Employees</a></li>
                                              <li><a href="/project">Projects</a></li>
                                              <li><a href="/cost">Costs</a></li>
                                              <li><a href="/timecards">Timecards</a></li>
                                              <li><a href="/logout">Logout</a>
                      [#else]
                          <li><a href="/login">Login</a></li>
                      [/#if]
      		</ol>


<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Edit/Add Employee</h3>
    </div>
    <div class="panel-body">

[#if errors??]
    [#list errors as error]
       <span style="color:red"> ${error}</span>
    <br>
    [/#list]
[/#if]
<form method="post" action="/employee/save">

    First name: <input name="firstName" type="input" value="${employee.firstName!''}">
    <br>
    Last name: <input name="lastName" type="input"  value="${employee.lastName!''}">
    <br>
    Birthdate: <input name="birthDate" type="input" value="[#if employee.birthDate??]${employee.birthDate?string('dd/MM/yyyy')}[/#if]">
    <br>
    JobTitle: <input name="jobTitle" type="input"  value="${employee.jobTitle!''}">
    <br>
    Gender: <input name="gender" type="input"  value="${employee.gender!''}">
    <br>
    EmploymentDate: <input name="employmentDate" type="input" value="[#if employee.employmentDate??]${employee.employmentDate?string('dd/MM/yyyy')}[/#if]">
    <br>

    [#if employee.id??]
        <input name="id" type="hidden" value="${employee.id?c}"/>
    [/#if]
    <input value="save" type="submit"/>
</form>

    </div>
</div>
</div>
[#include '/bootstrap_footer.ftl']
</body>
</html>




