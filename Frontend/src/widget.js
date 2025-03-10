  // Widget class to fetch recommendations and render them
  class RecommendationWidget {
    constructor(containerId, options = {}) {
      this.container = document.getElementById(containerId);
      // default configuration 
      const config = {
        apiKey: options.apiKey || "f9040ab1b9c802857aa783c469d0e0ff7e7366e4",
        appType: options.appType || "desktop",
        count: options.count || 4,
        sourceType: options.sourceType || "video",
        sourceId: options.sourceId || "demo-source",
        sourceUrl: window.location.href
      };
      
      // Object.assign(this.config, options);
      
      this.apiUrl = this.buildApiUrl(config);
      this.recommendations = [];
    }
  

    buildApiUrl(config) {
      return `http://api.taboola.com/1.0/json/taboola-templates/recommendations.get?` +
             `app.type=${config.appType}&` +
             `app.apikey=${config.apiKey}&` +
             `count=${config.count}&` +
             `source.type=${config.sourceType}&` +
             `source.id=${config.sourceId}&` +
             `source.url=${encodeURIComponent(config.sourceUrl)}`;
    }

    // Fetch recommendations from the Taboola API using provided parameters
    fetchRecommendations(retryCount = 0) {
      const MAX_RETRIES = 3;
      const RETRY_DELAY = 1000; // in milliseconds
      const FALLBACK_MESSAGE = 'No recommendations available.';
    
      return fetch(this.apiUrl)
        .then(response => response.json())
        .then(data => this.processResponseData(data, retryCount, MAX_RETRIES, RETRY_DELAY, FALLBACK_MESSAGE))
        .catch(error => Promise.reject(error));
    }
    
    processResponseData(data, retryCount, maxRetries, retryDelay, fallbackMessage) {
      const CONSOLE_WARNING_RETRYING =  "Received empty list, retrying..."; 
      const CONSOLE_WARNING_FALLBACK = "No recommendations available after retries."; 

      if (data && data.list) {
        if (data.list.length === 0 && retryCount < maxRetries) {
          console.warn(CONSOLE_WARNING_RETRYING);
          return new Promise(resolve =>
            setTimeout(() => resolve(this.fetchRecommendations(retryCount + 1)), retryDelay)
          );
        } else if (data.list.length === 0) {
          console.warn(CONSOLE_WARNING_FALLBACK);
          this.recommendations = [];
          this.showFallbackMessage(fallbackMessage);
        } else {
          this.recommendations = data.list.map(item => createRecommendation(item));
        }
      }
    }
    
    showFallbackMessage(message) {
      const listContainer = this.container.querySelector('#recommendation-list');
      if (listContainer) {
        listContainer.innerHTML = `<p style="margin: 0 auto; text-align: center;">${message}</p>`;
      }
    }
    

    

    // Render the list of recommendations
    render() {
      const listContainer = this.container.querySelector(
        "#recommendation-list"
      );

      // Render each recommendation and append to the list
      this.recommendations.forEach((rec) => {
        listContainer.appendChild(rec.render());
      });
    }

    // Initialize widget: fetch and then render recommendations
    init() {
      this.fetchRecommendations().then(() => {
        this.render();
      });
    }
  }
