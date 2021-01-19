/*
#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <fstream>
#include <string>
#include <cstdio>
#include <sstream>
#include <fstream>
#include <openssl/sha.h>

using namespace std;

void computeSHA256(const string& source, unsigned char* & output)
{
	SHA256_CTX ctx;

	SHA256_Init(&ctx);
	SHA256_Update(&ctx, source.c_str(), source.length());
	SHA256_Final(output, &ctx);
}

string bufferToHexString(const unsigned char* buffer)
{
	ostringstream outputter{""};
	auto* temp = new char[2];

	for (auto i = 0; i < SHA256_DIGEST_LENGTH; ++i)
	{
		sprintf(temp, "%02x", buffer[i]);
		outputter << temp;
	}

	return outputter.str();
}

int main()
{
	ifstream passwordFile("pass.txt");

	if (!passwordFile.is_open())
	{
		cout << "error while opening file" << endl;

		return -1;
	}

	string buffer{};

	auto* digestBuffer = new unsigned char[SHA256_DIGEST_LENGTH];

	constexpr auto VALUE_TO_COMPARE = "f50374f5acb53c120a6b5f65ad78fcf509ad174338be42db4e26944568e6ba20";

	ofstream outputFile("password_digest.txt");
	if (!outputFile.is_open())
	{
		cout << "error while opening output file" << endl;

		return -1;
	}

	while (!passwordFile.eof())
	{
		getline(passwordFile, buffer);

		computeSHA256(buffer, digestBuffer);

		string currValue = bufferToHexString(digestBuffer);

		outputFile << currValue << endl;

		if (currValue == VALUE_TO_COMPARE)
		{
			cout << "Found password: " << buffer;
		}
	}

	delete[] digestBuffer;

	return 0;
}
*/
