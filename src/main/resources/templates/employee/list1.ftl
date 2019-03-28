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
    			<li class="active"><a href="/">Home</a></li>
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

<img src="/ext-img/ValeaPoienii.jpg"/>

<div class="panel panel-default">
    <!-- Default panel contents -->
    <div class="panel-heading">Employee List:
        <div style="float:right"><a href="employee/add"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
    </div>

<table>
    <tr>
        <th style="min-width:150px">first name</th>
        <th style="min-width:150px">last name</th>
        <th style="min-width:150px">birthday</th>
        <th style="min-width:150px">job title</th>
        <th style="min-width:150px">employment date</th>
        <th style="min-width:150px">gender</th>
        <th style="min-width:150px"></th>

    </tr>

    [#list employees as employee]
    <tr>
        <td style="min-width:150px">${employee.firstName}</td>
        <td style="min-width:150px">${employee.lastName}</td>
        <td style="min-width:150px">${employee.birthDate?string('dd/MM/yyyy')}</td>
        <td style="min-width:150px">${employee.jobTitle}</td>
        <td style="min-width:150px">${employee.employmentDate?string('dd/MM/yyyy')}</td>
        <td style="min-width:150px">${employee.gender}</td>
        <td style="min-width:150px"><a href="/employee/edit?id=${employee.id?c}">Edit</a>
            <a href="/employee/delete?id=${employee.id?c}">Delete</a>
        </td>
    </tr>
    [/#list]
</table>

 </div>
</div>
</div>

</body>
</html>


