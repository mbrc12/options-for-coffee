# Options For Coffee

Options for Coffee is a simplified backtesting
tool (or toy) for trading algorithms. It works
by randomly generating stock prices (from specified parameters)
and running the strategy on it.

## Installation

Download the `.jar` file from the releases page, and execute it.
It may probably want permissions depending on your system (This type of file can harm your computer etc..).

##  Usage

OfC uses a [Geometric Brownian Motion](https://en.wikipedia.org/wiki/Geometric_Brownian_motion) to simulate 
stock paths, that is,

```
S(t) = S(0) exp(Q(t))
Q(t) = theta * t + sigma * W(t)
W(t) = Standard Brownian Motion
theta = mu - sigma^2 / 2
``` 

(Please note that, this model is the most basic of all models used to simulate stocks).

So, in the interface, under _Stock Properties_, you need to enter your desired value of mu and sigma.
You can check how a sample path looks like using _Show Sample Path_. For good results, it is recommended 
to use values of mu and sigma so that `theta` above is pretty small. Try out `mu = 0.006, sigma = 0.015` for
inspiration.

Now once you're happy with your paths, you need to write a strategy. Strategies for OfC are written in JS (ES5)
and are explained below. Once you write a strategy, select it, and set _Iterations_, and the _Risk Free Rate_. By default the Risk Free Rate is set
to 2.41% a year, which is a good default. But ofcourse, you're free to experiment.

When you test your strategy, OfC will take some time (**Issue**: for many days, and iterations, specifically when `days * iterations` is large, the execution blocks.
I need to put the evaluation in a separate thread or something).
After that, it will display on the left `Results` pane a graph
plotting the average position of your strategy, by day. On the
left pane you'll find the distribution of final returns of your strategy. You can also view
logs using _Console Logs_. However, note that the total log size is limited to 100,000 characters
for now, so please be frugal in your console output.
 
## Strategy

Strategies in OfC are written in JavaScript. Your file (name it whatever), has to implement
exactly one function, with the exactly following signature.

```
// You can change "agent" to whatever but 
// your function should be named "strategy"

function strategy(agent) { 
 // your strategy here
}
```

The interactor will call your function, by passing it an object which it
can use to retrieve data about the market. The list of actions / functions are listed below.
Note that your function will be called every'day', so your strategy should only 
implement the action for a day (depending on today's and past information received from the agent).

Here is a sample strategy, that is extremely bad:

```
function strategy(Q) {
	var curday = Q.getCurrentDay();
	
	if (curday >= 1 && Q.getStockPrice(curday) < Q.getStockPrice(curday - 1)) {
		Q.stock(0.1);
	} else {
		Q.stock(-0.1);
	}
}
```

### Interactor functions

The `agent` object is referred to as `Q` below.

| Method                                    | Description                                                                                                                         |
|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| Q.getStockPrice()                         | Returns current day stock price.                                                                                                    |
| Q.getStockPrice(day)                      | Returns stock price on given day. `day <= Q.getCurrentDay()`                                                                        |
| Q.getCurrentDay()                         | Returns current day.                                                                                                                |
| Q.stock(amount)                           | Amount of stock to buy. Set amount to negative if you want to sell.                                                                 |
| Q.callOption(direction, strike, maturity) | Buy/sell a European Call Option, with given strike rate and days to maturity. Set direction to Q.LONG or Q.SHORT for the direction. |
| Q.putOption(direction, strike, maturity)  | Buy/sell a European Put Option, with given strike rate and days to maturity. Set direction to Q.LONG or Q.SHORT for the direction.  |
| Q.getCallPrice(strike, maturity)          | Returns call option price for given strike and maturity.                                                                            |
| Q.getPutPrice(strike, maturity)           | Returns put option price for given strike and maturity.                                                                             |
| Q.log(loggingInfo)                        | Output loggingInfo to console. It will be logged as [Iteration # : Day #] : loggingInfo                                             |
| Q.getCurrentCash()                        | Returns the current cash position. Maybe negative if borrowing.                                                                     |     
| Q.getRiskFreeRate()                       | Returns the current risk free rate. This is the risk free rate you select divided by 360.                                           |  

## Bugs

It is expected that there will a lot of bugs / issues. Feel free to file them, or probably help me in fixing them. :).
