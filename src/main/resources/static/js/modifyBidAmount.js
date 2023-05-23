//increase or decrease bid amount by 50 units
window.onload = function () {
    const bidAmountInput = document.getElementById('bidAmount');
    const decrementButton = document.querySelector('.btn-bid-decrement');
    const incrementButton = document.querySelector('.btn-bid-increment');

    decrementButton.addEventListener('click', function () {
        const currentValue = parseFloat(bidAmountInput.value) || 0;
        bidAmountInput.value = Math.max(currentValue - 50, 0);
    });

    incrementButton.addEventListener('click', function () {
        const currentValue = parseFloat(bidAmountInput.value) || 0;
        bidAmountInput.value = currentValue + 50;
    });
};