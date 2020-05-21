function DisplayBlock(id, data, relate, index) {

    var svg = d3.select(id),
        margin = {left: 200, right: 100, top: 70, bottom: 200},
        hsvg = +svg.attr("height"),
        width = +svg.attr("width") - margin.left - margin.right;

    let scale_A = d3.scaleLinear().domain([data.A.min, data.A.max]).range([margin.left, margin.left + width]);
    let scale_B = d3.scaleLinear().domain([data.B.min, data.B.max]).range([margin.left, margin.left + width]);

    var chrWidth = width;			// width of chromosome
    var chrHeight = 20;		// height of chromosome
    var spaceBetweenChr = 220;	// distance between two chromosomes
    var proA = 1;
    var proB = 1;
    if ((data.B.max - data.B.min) > (data.A.max - data.A.min)) {
        proA = (data.B.max - data.B.min) / (data.A.max - data.A.min);
        if (proA > 70) {
            proA = 30
        }
    } else {
        proB = (data.A.max - data.A.min) / (data.B.max - data.B.min);
        if (proB > 70) {
            proB = 30
        }
    }

    // the related include the path for synteny links
    let relateD = relate.map(function (d) {
        let _d = [];
        _d.push([margin.top + chrHeight, data.A.rect[d[0] - 1][1]]);
        _d.push([margin.top + spaceBetweenChr, data.B.rect[d[1] - 1][1]]);
        _d.push([margin.top + spaceBetweenChr, data.B.rect[d[1] - 1][2]]);
        _d.push([margin.top + chrHeight, data.A.rect[d[0] - 1][2]]);
        return _d;
    });

    // for cover the images out of border when zoom
    svg.append("defs").append("clipPath")
        .attr("id", "clip")
        .append("rect")
        .attr('x', margin.left)
        .attr('y', margin.top)
        .attr("width", width)
        .attr("height", hsvg);

    // draw chr A and B as background
    svg.append('rect')
        .attr('x', margin.left)
        .attr('y', margin.top)
        .attr('width', chrWidth / proA)
        .attr('height', chrHeight)
        .style('stroke', '#4C84B7')
        .style('fill', '#5A9BD5');//#D7702A,#ED7C30

    svg.append('text')
        .attr('x', margin.left)
        .attr('y', margin.top - 25)
        .attr('text-anchor', 'start')
        .html(data.A.name);

    svg.append('text')
        .attr('x', margin.left - 190)
        .attr('y', margin.top + 13)
        .attr('text-anchor', 'start')
        .html(data.A.seqType + ":");

    svg.append('rect')
        .attr('x', margin.left)
        .attr('y', margin.top + spaceBetweenChr)
        .attr('width', chrWidth / proB)
        .attr('height', chrHeight)
        .style('stroke', '#4C84B7')
        .style('fill', '#5A9BD5');

    svg.append('text')
        .attr('x', margin.left)
        .attr('y', margin.top + 50 + spaceBetweenChr)
        .attr('text-anchor', 'start')
        .html(data.B.name);

    svg.append('text')
        .attr('x', margin.left - 190)
        .attr('y', margin.top + spaceBetweenChr + 13)
        .attr('text-anchor', 'start')
        .html(data.B.seqType + ":");


    let subRectG = svg.append('g');

    var drawFunction = function () {

        let chrA_start = subRectG.selectAll('.start_a').data([data.A.min]);
        chrA_start.enter().append('text')
            .classed('start_a', true)
            .merge(chrA_start)
            .attr('x', margin.left - 20)
            .attr('y', margin.top + 13)
            .attr('text-anchor', 'end')
            .html(data.A.min);
        chrA_start.exit().remove();

        let chrA_end = subRectG.selectAll('.end_a').data([data.A.max]);
        chrA_end.enter().append('text')
            .classed('end_a', true)
            .merge(chrA_end)
            .attr('x', margin.left + chrWidth / proA + 20)
            .attr('y', margin.top + 14)
            .attr('text-anchor', 'start')
            .html(data.A.max);
        chrA_end.exit().remove();

        let chrB_start = subRectG.selectAll('.start_b').data([data.B.min]);
        chrB_start.enter().append('text')
            .classed('start_b', true)
            .merge(chrB_start)
            .attr('x', margin.left - 20)
            .attr('y', margin.top + 13 + spaceBetweenChr)
            .attr('text-anchor', 'end')
            .html(data.B.min);
        chrB_start.exit().remove();

        let chrB_end = subRectG.selectAll('.end_b').data([data.B.max]);
        chrB_end.enter().append('text')
            .classed('end_b', true)
            .merge(chrB_end)
            .attr('x', margin.left + chrWidth / proB + 20)
            .attr('y', margin.top + 14 + spaceBetweenChr)
            .attr('text-anchor', 'start')
            .html(data.B.max);
        chrB_end.exit().remove();

        // draw
        let data_A = data.A.rect;
        let rect_A = subRectG.selectAll(".bar_a")
            .data(Object.keys(data_A));				// binding gene ID to selected .bar_a

        rect_A.enter().append('rect')				// enter: select DOM base on data, then create them by append
            .classed('bar_a', true)
            .merge(rect_A)							// position for genes in chrA
            .attr('x', d => (scale_A(data_A[d][1]) +
                ((scale_A(data_A[d][2]) - scale_A(data_A[d][1])) > 0 ? 0 : scale_A(data_A[d][2]) - scale_A(data_A[d][1]))
                - margin.left) / proA + margin.left) 					// 100
            .attr('y', margin.top)		// scale
            .attr('width', d => Math.abs(scale_A(data_A[d][2]) - scale_A(data_A[d][1])) / proA)
            .attr('height', chrHeight)
            .append("title").text(d => data.A.name + ":" + data_A[d][1] + "-" + data_A[d][2]);
        rect_A.exit().remove();						// exit: select DOM not in data, then remove them

        let data_B = data.B.rect;
        let rect_B = subRectG.selectAll(".bar_b")
            .data(Object.keys(data_B));

        rect_B.enter().append('rect')
            .classed('bar_b', true)
            .merge(rect_B)								// position for genes in chrB
            .attr('x', d => (scale_B(data_B[d][1]) +
                ((scale_B(data_B[d][2]) - scale_B(data_B[d][1])) > 0 ? 0 : scale_B(data_B[d][2]) - scale_B(data_B[d][1]))
                - margin.left) / proB + margin.left)	// 500
            .attr('y', margin.top + spaceBetweenChr)	// scale
            .attr('width', d => Math.abs(scale_B(data_B[d][2]) - scale_B(data_B[d][1])) / proB)
            .attr('height', chrHeight)
            .append("title").text(d => data.B.name + ":" + (data_B[d][1] > data_B[d][2] ? data_B[d][2] : data_B[d][1])
            + "-" + (data_B[d][2] >= data_B[d][1] ? data_B[d][2]:data_B[d][1]));
        rect_B.exit().remove();

        // draw path for synteny
        for (var i = 0; i < relateD.length; i++) {
            let path = d3.path();
            let r = relateD[i];

            path.moveTo((scale_A(r[0][1]) - margin.left) / proA + margin.left, r[0][0]);
            path.lineTo((scale_B(r[1][1]) - margin.left) / proB + margin.left, r[1][0]);
            path.lineTo((scale_B(r[2][1]) - margin.left) / proB + margin.left, r[2][0]);
            path.lineTo((scale_A(r[3][1]) - margin.left) / proA + margin.left, r[3][0]);
            path.closePath();

            var pathelement = subRectG.select('.synPath' + i);
            if (pathelement.empty()) pathelement = subRectG.append('a')
                .attr('href', "#hotspot" + index + (i + 1))
                .append('path').classed('synPath', true)
                .on("mouseover", function () {
                    d3.select(this).style('fill', 'grey').style("opacity", 1.0);
                })
                .on("mouseout", function () {
                    d3.select(this).style("fill", "grey").style("opacity", 0.5);
                });

            var b1 = parseInt(data_B[i][1]);
            var b2 = parseInt(data_B[i][2]);
            if(b1 > b2){
                b1 = b2;
                b2 = parseInt(data_B[i][1]);
            }
            pathelement.attr('d', path.toString())
                .append("title").text(data.A.name + ":" + data_A[i][1] + "-" + data_A[i][2]
                + "\n||||\n" +
                data.B.name + ":" + b1 + "-" + b2
            );
        }

    };

    drawFunction();

}

let num = 0;

function drawBar(seq, bar, height, max, yaxis, gen, index) {
    let cs = "";
    if (index === 0) {
        cs = "active";
        num += 1;
    } else {
        cs = ""
    }

    const ul = "<li role='presentation' class='" + cs + "' ><a href='#" + bar + "-" + num + "' id='" + bar + "-" + num + "-tab' role='tab' data-toggle='tab' " +
        "aria-controls='" + bar + "-" + num + "' aria-expanded='true'>" + bar + "</a></li>";
    $("#bar" + seq + " #myTab").append(ul);

    const li = "        <div role=\"tabpanel\" class=\"tab-pane fade in " + cs + "\" id='" + bar + "-" + num + "' aria-labelledby='" + bar + "-" + num + "-tab'>\n" +
        "            <div class=\"svg-container\" id=\"svg-con\">\n" +
        "                <div style=\"height: 80%;\n" +
        "                    float: left;\" id=\"yvalue\"></div>\n" +
        "                <div style=\"height: 80%;\n" +
        "                    float: left;\n" +
        "                    margin-left: 10px;\" id=\"coord\"></div>\n" +
        "            </div>\n" +
        "        </div>";

    $("#bar" + seq + " #myTabContent").append(li);

    let coord = "";
    let val = "";
    for (let i = 0; i < yaxis; i++) {
        coord += "<div class=\"y-axis\"></div>"
    }

    for (let i = 0; i <= yaxis; i++) {
        val += "<div class='y-data'>" + (max - max * i / yaxis)/1000000 + "Mb</div>"
    }

    const barid = "#bar" + seq + " #" + bar + "-" + num + " ";

    $(barid + "#coord").html(coord);
    $(barid + "#yvalue").html(val);
    $(barid + ".y-axis").css("height", 100 / yaxis + "%");
    $(barid + ".y-data").css("height", 100 / yaxis + "%");

    let html = "";
    $.each(height, function (i, v) {
        let h = (v / max) * 100 + "%";
        let svgid = i + 1;
        let chrid = "";
        if (i < 9) {
            chrid = "Chr0" + svgid;
        } else {
            chrid = "Chr" + svgid;
        }
        html += "<div class='svg'><div class='svg" + svgid + "' style='height:" + h + "'></div><span class='chr'>" + chrid + "</span></div>";
    });

    $(barid + "#svg-con").append(html);

    $.each(gen, function (i, v) {
        const name = v.name;
        const title = v.title;
        const bottom = (v.height / height[(v.chr - 1)]) * 100 + "%";
        $(barid + ".svg" + v.chr).append("<a href='#hit" + v.hitid + "' title='" + title + "' ><div class='gene' style='bottom:" + bottom + "'></div></a>");
    });
}