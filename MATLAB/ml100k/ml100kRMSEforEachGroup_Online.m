figure
filename = '../ml100k/onlineknn/predicate/ml100kItemsdistributionpredicate.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml100k/onlineknn/predicate/ml100kKemansdistributionpredicate.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../ml100k/onlineknn/predicate/ml100kHyrdistributionpredicate.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
D=[A(:,2)';B(:,2)';C(:,2)']';
bar(D);
axis([0 inf 0.5 1.1])
grid on;  
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600'});
legend('Item','Kmeans','HyRec','Location','northeast');
xlabel('the Number of clicks');
ylabel('RMSE')
title('RMSE on Test Data');