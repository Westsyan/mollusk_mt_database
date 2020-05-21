function $(param){
    if(arguments[1] == true){
        return document.querySelectorAll(param);
    }else{
        return document.querySelector(param);
    }
}
var $box = $(".box");
var $box1 = $(".box-1 ul li",true);
var $length = $box1.length;

var str = "";
for(var i =0;i<$length;i++){
    if(i==0){
        str +="<li class='on'>"+(i+1)+"</li>";
    }else{
        str += "<li>"+(i+1)+"</li>";
    }
}


var current = 0;

var timer;
timer = setInterval(go,5000);
function go(){
    for(var j =0;j<$length;j++){
        $box1[j].style.display = "none";
    }
    if($length == current){
        current = 0;
    }
    $box1[current].style.display = "block";
    current++;
}

for(var k=0;k<$length;k++){
    $box1[k].onmouseover = function(){
        clearInterval(timer);
    }
    $box1[k].onmouseout = function(){
        timer = setInterval(go,5000);
    }
}

