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
    <li class="active"><a href="/">Home</a></li>
                <li><a href="/employee">Employees</a></li>
                                    <li><a href="/project">Projects</a></li>
                                    <li><a href="/cost">Costs</a></li>
                                    <li><a href="/timecards">Timecards</a></li>
                                    <li><a href="/logout">Logout</a>
    		</ol>


<div class="panel panel-default">
    <!-- Default panel contents -->
    <div class="panel-heading">Timecard List:
        <div style="float:right"><a href="timecards/add"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
    </div>

<table>

    <tr>
        <th style="min-width:150px">date</th>
        <th style="min-width:150px">hours</th>
        <th style="min-width:150px">employee</th>
        <th style="min-width:150px">project</th>
        <th style="min-width:150px">comment</th>
        <th style="min-width:150px"></th>

    </tr>

    [#list timecards as timecard]
    <tr>
        <td style="min-width:150px">${timecard.date?string('dd/MM/yyyy')}</td>
        <td style="min-width:150px">${timecard.hours}</td>
        <td style="min-width:150px">
            [#if employeeNames??]
              [#list employeeNames?keys as key]
                  [#if key == timecard.employee_id?c]
                       ${employeeNames[key]!''}
                  [/#if]
              [/#list]
            [/#if]
        </td>
         <td style="min-width:150px">
                    [#if projectNames??]
                      [#list projectNames?keys as key]
                          [#if key == timecard.project_id?c]
                               ${projectNames[key]!''}
                          [/#if]
                      [/#list]
                    [/#if]
                </td>
        <td style="min-width:150px">${timecard.comment}</td>
        <td style="min-width:150px"><a href="/timecards/edit?id=${timecard.id?c}">Edit</a>
            <a href="/timecards/delete?id=${timecard.id?c}">Delete</a>
        </td>
    </tr>
    [/#list]




</table>

</div>
</div>
  </div>

</body>
</html>


