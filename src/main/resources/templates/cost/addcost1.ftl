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
    <div class="panel-heading">
        <h3 class="panel-title">Edit/Add Cost</h3>
    </div>
    <div class="panel-body">

[#if errors??]
    [#list errors as error]
       <span style="color:red"> ${error}</span>
    <br>
    [/#list]
[/#if]
<form method="post" action="/cost/save">

    Cost name: <input name="title" type="input" value="${cost.title!''}">
    <br>
    Cost: <input name="cost" type="input"  value="${cost.cost!''}">
    <br>

    [#if cost.id??]
        <input name="id" type="hidden" value="${cost.id?c}"/>
    [/#if]
    <input value="save" type="submit"/>
</form>

    </div>
</div>
</div>
[#include '/bootstrap_footer.ftl']
</body>
</html>