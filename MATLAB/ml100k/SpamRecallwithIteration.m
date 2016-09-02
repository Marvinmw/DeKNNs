figure
filename = '../ml100k/Spam/emailItemSpamRecallAccuracy.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml100k/Spam/emailHyrecSpamRecallAccuracy.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);

plot(A(:,1),A(:,7),'b--^',B(:,1),B(:,7),'r:o','MarkerSize',4);
legend('Items','HyRec','Location','Southeast');
xlabel('Iteration');
ylabel('KNN-Recall')
title('KNN-Recall with the Iteration in Spam data');

figure
filename = '../ml100k/Spam/emailItemSpamRecallAccuracy.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml100k/Spam/emailHyrecSpamRecallAccuracy.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);

plot(A(:,1),A(:,6),'b--^',B(:,1),B(:,6),'r:o','MarkerSize',4);
legend('Items-Sample','HyRec-Sample','Location','Southeast');
xlabel('Iteration');
ylabel('Recall')
title('Recall');