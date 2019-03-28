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
    			<li class="active">Home</li>
                <li><a href="/employee">Employees</a></li>
                                    <li><a href="/project">Projects</a></li>
                                    <li><a href="/cost">Costs</a></li>
                                    <li><a href="/timecards">Timecards</a></li>
                                    <li><a href="/logout">Logout</a>
    		</ol>


    		<h3>
           Project Details

            </h3>
             [#if projectCost??]
            ${projectCost.name} - ${projectCost.cost} EUR
            [/#if]
</div>
</div>

</body>
</html>


