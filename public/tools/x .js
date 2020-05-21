columns:[{
    field:"species",
    title:"Species",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2,
    formatter:function(value,row){
        return "<a href='#' style='color: cornflowerblue;'>" +  row.species +"</a>"
    }
},{
    field:"phylum",
    title:"Phylum",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"class",
    title:"Class",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"order",
    title:"Order",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"order",
    title:"Order",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"family",
    title:"Family",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"genus",
    title:"Genus",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"location",
    title:"Location",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"genbank",
    title:"GenBank",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"bp",
    title:"BP",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2
},{
    field:"ncbilink",
    title:"NCBI",
    align:"center",
    halign:"center",
    yalign:"middle",
    rowspan: 2 ,
    formatter:function (value,row) {
    var array = row.ncbilink.split(";");
    var location = "";
    $.each(array,function (index,value) {
        if(array.length > 1){
            location +=  "<a target='_blank' href='" + value + "'>link" + (index +1) + "</a><br/>";
        }else{
            location +=  "<a target='_blank' href='" + value + "'>link</a>";
        }
    });
    return location;
}
}
]