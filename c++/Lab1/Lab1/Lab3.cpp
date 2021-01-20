/*
#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <cstdio>
#include <fstream>

#include <openssl/applink.c>
#include <openssl/pem.h>
#include <openssl/rsa.h>

using namespace std;

int main()
{
	constexpr auto RSA_SEED = 65535;

	RSA* rsaKeyPair = nullptr;

	rsaKeyPair = RSA_new();
	rsaKeyPair = RSA_generate_key(2048, RSA_SEED, nullptr, nullptr);

	RSA_check_key(rsaKeyPair);

	FILE* privateKeyFile = fopen("privateKeyFile.pem", "w+");
	if (!privateKeyFile)
	{
		cout << "can't open private key file" << endl;
		return -1;
	}


	PEM_write_RSAPrivateKey(privateKeyFile, rsaKeyPair, nullptr, nullptr, 0, nullptr, nullptr);
	fclose(privateKeyFile);

	FILE* publicKeyFile = fopen("publicKeyFile.pem", "w+");
	if (!publicKeyFile)
	{
		cout << "can't open public key file" << endl;
		return -1;
	}

	PEM_write_RSAPublicKey(publicKeyFile, rsaKeyPair);
	fclose(publicKeyFile);

	RSA_free(rsaKeyPair);

	cout << "RSA key pair successfully generated!" << endl;

	return 0;
}
*/
