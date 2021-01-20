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

	string originalText = "hello world";

	auto* cipherText = new unsigned char[RSA_size(publicKey)];

	/*
	 * encrypt in chunks of RSA_size(publicKey)
	 * pad only the last chunk
	 */

	RSA_public_encrypt(originalText.length() + 1, (unsigned char*)originalText.c_str(), cipherText, publicKey,
	                   RSA_PKCS1_PADDING);
	

	auto* plainText = new unsigned char[RSA_size(privateKey)];

	RSA_private_decrypt(RSA_size(publicKey), cipherText, plainText, privateKey, RSA_PKCS1_PADDING);

	cout << plainText;

	delete[] plainText;
	delete[] cipherText;

	RSA_free(privateKey);
	RSA_free(publicKey);

	return 0;
}
