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
    <div class="panel-heading">Projects List:
        <div style="float:right"><a href="project/add"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
    </div>


<table>
    <tr>
        <th style="min-width:150px">name</th>
        <th style="min-width:150px">description</th>

        <th style="min-width:150px"></th>

    </tr>

    [#list projects as project]
    <tr>
        <td style="min-width:150px">${project.name}</td>
        <td style="min-width:150px">${project.description}</td>

        <td style="min-width:150px"><a href="/project/edit?id=${project.id?c}">Edit</a>
            <a href="/project/delete?id=${project.id?c}">Delete</a>
             <a href="/project/calculateCost?id=${project.id?c}">   Calculate Cost</a> [#if totalCost??]${totalCost}[/#if]
        </td>
    </tr>
    [/#list]
</table>

 </div>
</div>
</div>
[#include '/bootstrap_footer.ftl']
</body>
</html>

