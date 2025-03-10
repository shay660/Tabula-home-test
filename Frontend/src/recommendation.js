// Class representing an individual recommendation item
class Recommendation {
    constructor(data) {
      // this.branding = data.branding;
      this.origin = data.origin; 
      this.type = data.type;
      this.url = data.url.trim();
      this.name = data.name;
      // Assuming thumbnail is an array with at least one object
      this.thumbnail =
        data.thumbnail && data.thumbnail[0]
          ? data.thumbnail[0].url.trim()
          : "";
    }

    // Create and return the HTML element for this recommendation
    render() {
      const container = document.createElement("div");
      container.classList.add("recommendation");

      // Create image element for the thumbnail
      const img = document.createElement("img");
      img.src = this.thumbnail;
      img.alt = this.name;

      // Create a container for details
      const details = document.createElement("div");
      details.classList.add("details");

      // Caption (the article title)
      const caption = document.createElement("p");
      caption.textContent = this.name;
      details.appendChild(caption);

      container.appendChild(img);
      container.appendChild(details);

      return container; 
    }
}

class OrganicRecommendation extends Recommendation {
    render() {
        const container = super.render(); 
        // Set click behavior
        container.addEventListener('click', () => {window.location.href = this.url}); 
        return container
    }
}


class SponsoredRecommendation extends Recommendation {
    constructor(data) {
      super(data); 
      this.branding = data.branding; 

    }

    render() {
      const container = super.render(); 

      // Add publisher 
      this.addSponsoredBrand(container);

      // Open the sponsored website in a new tab.  
      container.addEventListener("click", () => {window.open(this.url, "_blank")});

      return container; 
    }

    addSponsoredBrand(container) {
      container.classList.add("Sponsored");
      const advertiser = document.createElement("small");
      advertiser.textContent = "Sponsored by: " + this.branding;
      container.querySelector('.details').appendChild(advertiser); 
    }
}


function createRecommendation(data) {
    let recomendationWidget;
    if (data.origin === "sponsored") {
        recomendationWidget = new SponsoredRecommendation(data);
    } else if (data.origin === "organic") {
        recomendationWidget = new OrganicRecommendation(data); 
    }
    return recomendationWidget;   
} 