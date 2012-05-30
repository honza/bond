// Bond - the spy agent
//
// This file is part of Bond.
// Bond is licensed under the terms of the BSD license.
// (c) 2012 - Honza Pokorny - All rights reserved

(function() {

  var toEpoch = function(date) {
    // TODO: Implement this server side?
    var letters = date.split('');
    var year = parseInt(letters.slice(0, 4).join(''), 10);
    var month = parseInt(letters.slice(4, 6).join(''), 10);
    var day = parseInt(letters.slice(6, 8).join(''), 10);
    var d = new Date(year, month - 1, day);
    return d.getTime() / 1000;
  };

  var d = data || {};
  var cleanData = [];

  var palette = new Rickshaw.Color.Palette();

  var makeSeries = function(data) {
    var keys = Object.keys(data);
    var series = [];

    for (var i = 0; i < keys.length; i++) {
      var serie = {
        color: palette.color(),
        name: keys[i],
        data: []
      };
      for (var k = 0; k < data[keys[i]].length; k++) {
        serie.data.push({
          x: toEpoch(data[keys[i]][k].added),
          y: data[keys[i]][k].count
        });
      }
      series.push(serie);
    }

    return series;
  };

  var graph = new Rickshaw.Graph({
    element: document.querySelector("#chart"),
    width: 960,
    height: 600,
    renderer: 'line',
    series: makeSeries(data)
  });

  var y_axis = new Rickshaw.Graph.Axis.Y({
    graph: graph,
    orientation: 'left',
    tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
    element: document.getElementById('y_axis')
  });

  var axes = new Rickshaw.Graph.Axis.Time({graph: graph});

  var legend = new Rickshaw.Graph.Legend({
    element: document.querySelector('#legend'),
    graph: graph
  });

  var hoverDetail = new Rickshaw.Graph.HoverDetail({
    graph: graph
  });


  graph.render();

}).call(this);
