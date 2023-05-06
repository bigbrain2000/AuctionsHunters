document.addEventListener("DOMContentLoaded", function () {
    const priceFromSelect = document.getElementById("priceFrom");
    const priceToSelect = document.getElementById("priceTo");

    function addPriceOptions(selectElement, startPrice, endPrice, increment) {
        for (let price = startPrice; price <= endPrice; price += increment) {
            const option = document.createElement("option");
            option.value = price;
            option.text = price + " RON";
            selectElement.add(option);
        }
    }

    addPriceOptions(priceFromSelect, 1000, 10000, 1000);
    addPriceOptions(priceFromSelect, 10000, 100000, 10000);

    addPriceOptions(priceToSelect, 1000, 10000, 1000);
    addPriceOptions(priceToSelect, 10000, 100000, 10000);
});
