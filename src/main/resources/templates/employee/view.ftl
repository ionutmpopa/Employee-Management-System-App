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

        <ol class="breadcrumb">
        <li class="active"><a href="/employee/view">Home</a></li>
                    <li><a href="/timecard">Timecards</a></li>
        		</ol>

<div class="panel panel-default">
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

    <tr>
        <td style="min-width:150px">${employee.firstName!''}</td>
        <td style="min-width:150px">${employee.lastName!''}</td>
        <td style="min-width:150px">[#if employee.birthDate??]${employee.birthDate?string('dd/MM/yyyy')}[/#if]</td>
        <td style="min-width:150px">${employee.jobTitle!''}</td>
        <td style="min-width:150px">[#if employee.birthDate??]${employee.employmentDate?string('dd/MM/yyyy')}[/#if]</td>
        <td style="min-width:150px">${employee.gender!''}</td>
        </td>
    </tr>
</table>

    </div>
</div>
   </div>
[#include '/bootstrap_footer.ftl']
</body>
</html>