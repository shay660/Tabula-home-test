  /**
   * ************** TASKS ****************
   * 4. to check options about changing the widget place\size.
   * 6. think how I want to submit this. 
   
   * 
   * ********** COMPLETE TASKS ***************
   * 1. make the code more general. Maybe by seperate each recomendation type to it's
   * own class - OOP.
   * 2. check what is the deal with the apikey - can be more general? 
   * 3. split the code into fuuctins for readabilty (only after 1 is complete)
   * 5. check if the code work at mobile as well.
   * 7. add more tests. 
   */


// Wait for the DOM to be fully loaded, then initialize the widget
document.addEventListener('DOMContentLoaded', function() {
    const widget = new RecommendationWidget('recommendation-widget', {
      apiKey: 'f9040ab1b9c802857aa783c469d0e0ff7e7366e4',
      publisherId: 'taboola-templates',    
      sourceId: 'your-source-id-here',     
      count: 4                             
    });
    widget.init();
});



