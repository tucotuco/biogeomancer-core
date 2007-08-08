// dhtmlUtils.js

function findObj(n, d) { //v4.01
  var p,i,x;  
  if(!d) d=document; 
  if((p=n.indexOf("?"))>0 && parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
  }
  if(!(x=d[n])&&d.all) x=d.all[n]; 
  for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); 
  return x;
}

function imgSwap(event, grpName) { //v6.0
  var i;
  var img;
  var nbArr;
  var args=imgSwap.arguments;
  if (event == "over") {    
    document.nbOver = nbArr = new Array();
    if ((img = findObj(args[1])) != null) {
      if (!img.up) {
        img.up = img.src;
      }
      if (img.dn) {
          img.src = args[3];
      } else {
          img.src = args[2];
      } 
      nbArr[0] = img;
    }
  } else if (event == "out" ) {
    img = document.nbOver[0]; 
    if (img.dn) {
        img.src = img.dn;
    } else {
        img.src = img.up;
    }
  } else if (event == "down") {
    if (parent.mapFrame.ready) {
      nbArr = document[grpName];
      if (nbArr) {
        img=nbArr[0]; 
        img.src = img.up; 
        img.dn = 0; 
      }
      document[grpName] = nbArr = new Array();
      if ((img = findObj(args[2])) != null) {
        if (!img.up) img.up = img.src;
        if (args[3]) {
          img.src = img.dn = args[3];
        } else {
          img.src = img.up;
        }
        nbArr[0] = img;
      } 
    }
  }
}
