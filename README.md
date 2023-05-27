# rsi-tradingview
Tradingview RSI implementation

This flavour of RSI is guaranteed to converge to default one used by Tradingview (and thus the one that is shown on most trading interfaces). 

Check test for usage example, the general idea is:
1. collect some historical data to initialize an RSI object.
2. feed online data to initialized RSI object to get real time RSI value.
