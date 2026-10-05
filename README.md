# udp-exchange-rate
Bước 1: mở terminal tại thư mục dự án
cd g:\hoc_tap\ltm\exchange_rate_UDP
Bước 2: tạo thư mục bin (nếu chưa có)
mkdir bin
Bước 3: biên dịch tất cả các file Java
javac -d bin -sourcepath src src/com/exchangerate/common/*.java src/com/exchangerate/server/*.java src/com/exchangerate/client/gui/*.java src/com/exchangerate/client/*.java
Bước 4: chạy server (mở terminal 1)
java -cp bin com.exchangerate.server.ExchangeRateServer
Bước 5: chạy client (mở terminal 2)
cd g:\hoc_tap\ltm\exchange_rate_UDP
java -cp bin com.exchangerate.client.ExchangeRateClient
