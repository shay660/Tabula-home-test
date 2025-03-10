// Test suite for Recommendation classes and RecommendationWidget

function runTests() {
  // Test data for organic and sponsored recommendations
  const organicData = {
    origin: "organic",
    type: "article",
    url: "http://example.com/organic",
    name: "Organic Article",
    thumbnail: [{ url: "http://example.com/image_organic.jpg" }]
  };

  const sponsoredData = {
    origin: "sponsored",
    type: "article",
    url: "http://example.com/sponsored",
    name: "Sponsored Article",
    thumbnail: [{ url: "http://example.com/image_sponsored.jpg" }],
    branding: "BrandX"
  };

  // Test 1: OrganicRecommendation
  const organicRec = new OrganicRecommendation(organicData);
  const organicElement = organicRec.render();
  console.assert(organicElement instanceof HTMLElement, "OrganicRecommendation: render() should return an HTMLElement");
  console.assert(organicElement.querySelector("p").textContent === organicData.name, "OrganicRecommendation: Caption text should match");
  
  // Test 2: SponsoredRecommendation
  const sponsoredRec = new SponsoredRecommendation(sponsoredData);
  const sponsoredElement = sponsoredRec.render();
  console.assert(sponsoredElement instanceof HTMLElement, "SponsoredRecommendation: render() should return an HTMLElement");
  const smallElement = sponsoredElement.querySelector("small");
  console.assert(smallElement && smallElement.textContent.indexOf("Sponsored by:") !== -1, "SponsoredRecommendation: Should display branding info");

  // Test 3: Factory function createRecommendation
  const recFromFactoryOrganic = createRecommendation(organicData);
  console.assert(recFromFactoryOrganic instanceof OrganicRecommendation, "Factory: Should return OrganicRecommendation for organic data");
  const recFromFactorySponsored = createRecommendation(sponsoredData);
  console.assert(recFromFactorySponsored instanceof SponsoredRecommendation, "Factory: Should return SponsoredRecommendation for sponsored data");

  // Test 4: RecommendationWidget API URL building
  const testOptions = {
    apiKey: "test-api-key",
    appType: "mobile",
    count: 5,
    sourceType: "article",
    sourceId: "test-source",
    sourceUrl: "http://test.com"
  };

  // Create a dummy container for widget tests
  const dummyContainer = document.createElement("div");
  dummyContainer.id = "dummy-widget";
  dummyContainer.innerHTML = '<div id="recommendation-list"></div>';
  document.body.appendChild(dummyContainer);

  const widget = new RecommendationWidget("dummy-widget", testOptions);
  const builtUrl = widget.buildApiUrl({
    apiKey: testOptions.apiKey,
    appType: testOptions.appType,
    count: testOptions.count,
    sourceType: testOptions.sourceType,
    sourceId: testOptions.sourceId,
    sourceUrl: testOptions.sourceUrl
  });
  console.assert(builtUrl.indexOf(`app.apikey=${testOptions.apiKey}`) !== -1, "Widget: API URL should include the correct apiKey");
  console.assert(builtUrl.indexOf(`app.type=${testOptions.appType}`) !== -1, "Widget: API URL should include the correct appType");

  // Test 5: showFallbackMessage
  widget.showFallbackMessage("Test fallback");
  const fallbackMsg = dummyContainer.querySelector("#recommendation-list p");
  console.assert(fallbackMsg && fallbackMsg.textContent === "Test fallback", "Widget: Fallback message should be displayed correctly");

  // Clean up dummy container after tests
  document.body.removeChild(dummyContainer);

  console.log("All tests passed!");
}

// Run tests when DOM is ready
document.addEventListener("DOMContentLoaded", runTests);
