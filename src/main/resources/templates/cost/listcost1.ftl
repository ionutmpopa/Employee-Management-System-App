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
    <div class="panel-heading">Cost List:
        <div style="float:right"><a href="cost/add"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
    </div>
<table>
    <tr>
        <th style="min-width:150px">title</th>
        <th style="min-width:150px">cost</th>

    </tr>

    [#list costs as cost]
    <tr>
        <td style="min-width:150px">${cost.title}</td>
        <td style="min-width:150px">${cost.cost}</td>
        <td style="min-width:150px"><a href="/cost/edit?id=${cost.id?c}">Edit</a>
            <a href="/cost/delete?id=${cost.id?c}">Delete</a>
        </td>
    </tr>
    [/#list]
</table>

 </div>
</div>
</div>

</body>
</html>