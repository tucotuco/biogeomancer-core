<p>
<b>Email Address:</b>&nbsp;<%= request.getAttribute("text") %>
</p>
<p>
<b>The Query Parameter:</b>&nbsp;<%= request.getAttribute("queryValue") %>
</p>
<p>
<b>The File name:</b>&nbsp;<%= request.getAttribute("fileName") %>
</p>
<p>
<b>The File content type:</b>&nbsp;<%= request.getAttribute("contentType") %>
</p>
<p>
<b>The File size:</b>&nbsp;<%= request.getAttribute("size") %>
</p>
<p>
<p>
<b>The number of records uploaded:</b>&nbsp;<%= request.getAttribute("numrecs") %>
</p>
<b>The File has been stored as:</b>
</p>
<hr />
<pre>
<%= request.getAttribute("data") %>
</pre>
<hr />
