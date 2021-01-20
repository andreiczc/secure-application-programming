#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <cstdio>

#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/applink.c>

using namespace std;

int main()
{
	FILE* privateKeyFile = fopen("privateKeyFile.pem", "rb");
	if (!privateKeyFile)
	{
		cout << "can't open private key file" << endl;
		return -1;
	}

	RSA* privateKey = nullptr;
	privateKey = PEM_read_RSAPrivateKey(privateKeyFile, &privateKey, nullptr, nullptr);

	fclose(privateKeyFile);

	FILE* publicKeyFile = fopen("publicKeyFile.pem", "rb");
	if (!publicKeyFile)
	{
		cout << "can't open public key file" << endl;
		return -1;
	}

	RSA* publicKey = nullptr;
	publicKey = PEM_read_RSAPublicKey(publicKeyFile, &publicKey, nullptr, nullptr);

	fclose(publicKeyFile);

	auto* originalText = new char[512];
	originalText[511] = '\0';
	char currChar = 'a';
	for (int i = 0; i < 511; ++i)
	{
		originalText[i] = currChar;
		if (currChar == 'z') currChar = 'a';
		else ++currChar;
	}


	/*
	 * encrypt in chunks of RSA_size(publicKey)
	 * pad only the last chunk
	 */
	const auto noChunks = (strlen(originalText) + 1) / RSA_size(publicKey);
	const auto lastBlockSize = (strlen(originalText) + 1) % RSA_size(publicKey);

	auto* cipherText = new unsigned char[RSA_size(publicKey) * (noChunks + 1)];

	for (auto i = 0; i < noChunks; ++i)
	{
		RSA_public_encrypt(RSA_size(publicKey), (unsigned char*)(&originalText[RSA_size(publicKey) * i]),
		                   &cipherText[RSA_size(publicKey) * i], publicKey,RSA_NO_PADDING);
	}
	// do final
	if (lastBlockSize)
	{
		RSA_public_encrypt(lastBlockSize, (unsigned char*)(&originalText[noChunks]), &cipherText[noChunks], publicKey,
		                   RSA_PKCS1_PADDING);
	}


	auto* plainText = new unsigned char[RSA_size(publicKey) * (noChunks + 1)];

	for (auto i = 0; i < noChunks; ++i)
	{
		RSA_private_decrypt(RSA_size(publicKey), &cipherText[RSA_size(publicKey) * i],
		                    &plainText[RSA_size(publicKey) * i], privateKey,
		                    RSA_NO_PADDING);
	}
	if (lastBlockSize)
	{
		RSA_private_decrypt(RSA_size(publicKey), &cipherText[noChunks], &plainText[noChunks], privateKey,
		                    RSA_PKCS1_PADDING);
	}

	cout << plainText;

	delete[] plainText;
	delete[] cipherText;

	RSA_free(privateKey);
	RSA_free(publicKey);

	return 0;
}
