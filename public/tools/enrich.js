$(function () {
    AutoGene();
    egGene();
    goTable();
    keggTable();
    formValidation();
});

function change(element) {
    var value = $(element).find(">option:selected").val();
    if (value == "text") {
        $("#queryContent").show();
        $("#queryFile").hide()
    } else {
        $("#queryContent").hide();
        $("#queryFile").show()
    }
}


$("#egFile").click(function () {
    var species = $("#db").val();
    var example = getExample(species);
    var e = "";
    $.each(example.trim().split(','), function (i, v) {
        e += v.trim() + "\n";
    });
    var fileName = "example.txt";
    var blob = new Blob([e], {
        type: "text/plain;charset=utf-8"
    });
    saveAs(blob, fileName)
});

function goTable() {
    var array = ["ID", "Enrichment", "Description", "Ratio_In_Study", "Ratio_In_Pop", "P_Uncorrected",
        "P_Bonferroni", "P_Holm", "P_Sidak", "P_Fdr", "Namespace", "Genes_In_Study"];
    var values = ["id", "enrichment", "description", "ratio_in_study", "ratio_in_pop", "p_uncorrected",
        "p_bonferroni", "p_holm", "p_sidak", "p_fdr", "namespace", "genes_in_study"];
    var html = "";
    $.each(array, function (n, value) {
            html += "<label style='margin-right: 15px'>" +
                "<input type='checkbox' checked='checked' value='" + values[n] + "' onclick=\"setGoColumns('" + values[n] + "')\">" + value +
                "</label>"
        }
    );
    $("#goCheckbox").append(html);

    $("#goTable").bootstrapTable();
    var hiddenArray = ["p_bonferroni", "p_holm", "p_sidak", "p_fdr", "genes_in_study"];
    $.each(hiddenArray, function (n, value) {
        $('#goTable').bootstrapTable('hideColumn', value);
        $("input:checkbox[value=" + value + "]").attr("checked", false)
    });
}

function setGoColumns(value) {
    var element = $("input:checkbox[value=" + value + "]");
    if (element.is(":checked")) {
        $('#goTable').bootstrapTable('showColumn', value);
    } else {
        $('#goTable').bootstrapTable('hideColumn', value);
    }
}

function keggTable() {
    var array = ["Database", "ID", "Input number", "Background number", "P-Value", "Corrected P-Value", "Input", "Hyperlink"];
    var values = ["database", "id", "input_num", "back_num", "p-value", "correct_pval", "input", "hyperlink"];
    var html = "";
    $.each(array, function (n, value) {
            html += "<label style='margin-right: 15px'>" +
                "<input type='checkbox' checked='checked' value='" + values[n] + "' onclick=\"setKeggColumns('" + values[n] + "')\">" + value +
                "</label>"
        }
    );
    $("#keggCheckbox").append(html);

    $('#keggTable').bootstrapTable();
    var hiddenArray = ["correct_pval", "input"];
    $.each(hiddenArray, function (n, value) {
        $('#keggTable').bootstrapTable('hideColumn', value);
        $("input:checkbox[value=" + value + "]").attr("checked", false)
    });
}

function setKeggColumns(value) {
    var element = $("input:checkbox[value=" + value + "]");
    if (element.is(":checked")) {
        $('#keggTable').bootstrapTable('showColumn', value);
    } else {
        $('#keggTable').bootstrapTable('hideColumn', value);
    }
}

function formValidation() {
    $('#form').formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            gene: {
                validators: {
                    notEmpty: {
                        message: 'This is not be empty!'
                    }
                }
            },
            pValue: {
                validators: {
                    notEmpty: {
                        message: 'This is not be empty!'
                    },
                    numeric: {
                        message: 'The p-value must be a number!'
                    }
                }
            }
        }
    });
}

function Running() {
    $("#goResult").hide();
    $("#keggResult").hide();
    var form = $("#form");
    var fv = form.data("formValidation");
    fv.validate();
    if (fv.isValid()) {
        $("#result").hide();
        var index = layer.load(1, {
            shade: [0.1, '#fff']
        });

        $("#run").attr("disabled", true).html("Running...");

        $.ajax({
            url: "/MRDB/tools/enrichment",
            type: "post",
            dataType: "json",
            processData: false,
            contentType: false,
            data: new FormData($("#form")[0]),
            success: function (res) {
                if(res.status === 0){
                    swal("Error", res.message, "error");
                }else{
                    var method = $("#method").val();
                    $("#" + method + "Table").bootstrapTable("load", res.data);
                    $("#" + method + "Result").show();
                }
                $("#run").attr("disabled", false).html("Run").blur();
                layer.close(index)
            }
        });
    }
}
