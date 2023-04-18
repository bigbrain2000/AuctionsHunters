//increase or decrease bid amount by 50
document.addEventListener('DOMContentLoaded', function () {
    const bidAmountInput = document.getElementById('minimumPrice');
    const decrementButton = document.querySelector('.btn-bid-decrement');
    const incrementButton = document.querySelector('.btn-bid-increment');

    decrementButton.addEventListener('click', function () {
        const currentValue = parseFloat(bidAmountInput.value) || 0;
        bidAmountInput.value = Math.max(currentValue - 5, 0);
    });

    incrementButton.addEventListener('click', function () {
        const currentValue = parseFloat(bidAmountInput.value) || 0;
        bidAmountInput.value = currentValue + 50;
    });
});
