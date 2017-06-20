<%@ page import="jku.students.at.OWLHandler" %><%--
  Created by IntelliJ IDEA.
  User: Florian
  Date: 20/06/2017
  Time: 01:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TEST</title>
</head>
<body>
<h1>MiniProject 2 Semantic Web</h1>

<button type="button" onclick="location.href='addIndividuum.jsp'">Add Person</button>

<form action="administerStudent.jsp" method="post">

    <div class="divTable">
        <div class="divTableBody">

            <div class="divTableRow">
                <div class="divTableCellFirstColumn">Student:</div>
                <div class="divTableCell">
                    <select name="class">
                        <%
                            for (String student: OWLHandler.getInstance().getIndividuen("Student")){
                                out.println("<option value=\"" + student  + "\" selected>" + student + "</option>");
                            }
                        %>
                    </select>
                </div>
            </div>

    </div>
</form>

</body>
</html>
