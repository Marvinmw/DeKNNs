clear all
figure
filename = '../ml10m/online/ml10mItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
B=A(15:end,2);
C=sum(B);
D=A(1:14,2);
D(15)=C;
% h = histogram(A(:,2),10);
bar(D,0.5);
grid on
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600','600-640','640-680','680-720','720-760','>760'});

title('User Distribution on Ml10m');
xlabel('Number of Clicks');
ylabel('Number of Users');