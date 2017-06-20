<%@ page import="java.util.ArrayList" %>
<%@ page import="jku.students.at.OWLHandler" %><%--
  Created by IntelliJ IDEA.
  User: Florian
  Date: 20/06/2017
  Time: 17:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>AddIndividuum</title>
</head>
<body>
<form action="addIndividuum.jsp" method="post">

    <%
        String name = request.getParameter("name");
        if (name != null){
            String cl = request.getParameter("class");
            if (OWLHandler.getInstance().addIndividuum(name,cl)){
                out.println(cl + ": " + name + " wurde hinzugefügt!");
                OWLHandler.getInstance().saveOntology();
            }
            else {
                out.println("Individuum konnte nicht hinzugefügt werden. Consistency wahrscheinlich nicht in Ordnung!");
            }
        }


    %>

    <div class="divTable">
        <div class="divTableBody">
            <div class="divTableRow">
                <div class="divTableCellFirstColumn">Name:</div>
                <div class="divTableCell">
                    <input type="text" name="name" value="">
                </div>
            </div>
            <div class="divTableRow">
                <div class="divTableCellFirstColumn">Class:</div>
                <div class="divTableCell">
                    <select name="class">
                        <%
                            for (String className : OWLHandler.getInstance().getAllClasses()){
                                out.println("<option value=\"" + className  + "\" selected>" + className + "</option>");
                            }
                        %>

                    </select>
                </div>
            </div>
        </div>

    </div>
    <input type="submit" name="addIndividuum" value="Add">
    <button type="button" onclick="location.href='index.jsp'">Home</button>

</form>
</body>
</html>
