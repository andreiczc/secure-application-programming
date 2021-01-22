#include <iostream>
#include <string>
#include <filesystem>
#include <fstream>
#include <openssl/sha.h>
#include <openssl/evp.h>
#include <openssl/aes.h>

using namespace std;
namespace fs = filesystem;

unique_ptr<unsigned char> computeSha256(const void* originalMessage, const int messageSize)
{
	unique_ptr<unsigned char> digest(new unsigned char[SHA256_DIGEST_LENGTH]);

	SHA256_CTX ctx;
	SHA256_Init(&ctx);

	if (!SHA256_Update(&ctx, originalMessage, messageSize) || !SHA256_Final(digest.get(), &ctx))
	{
		digest.reset(nullptr);
		printf("error computing sha256\n");
	}

	return digest;
}

bool equalDigests(const unsigned char* v1, const unsigned char* v2)
{
	for (auto i = 0; i < SHA256_DIGEST_LENGTH; ++i)
	{
		if (v1[i] != v2[i]) return false;
	}

	return true;
}

unique_ptr<unsigned char> encryptAes256Cbc(const unsigned char* in, const int inLength, int& outLength,
                                           const unsigned char* key, const unsigned char* iv)
{
	const auto noBlocks = inLength % AES_BLOCK_SIZE ? inLength / AES_BLOCK_SIZE + 1 : inLength / AES_BLOCK_SIZE;

	auto* cipherText = new unsigned char[AES_BLOCK_SIZE * noBlocks];

	outLength = 0;
	int temp = 0;

	auto* ctx = EVP_CIPHER_CTX_new();

	EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), nullptr, key, iv);

	EVP_EncryptUpdate(ctx, cipherText, &outLength, in, inLength);

	EVP_EncryptFinal_ex(ctx, cipherText + outLength, &temp);
	outLength += temp;

	EVP_CIPHER_CTX_free(ctx);

	return unique_ptr<unsigned char>(cipherText);
}

unique_ptr<unsigned char> decryptAes256Cbc(const unsigned char* in, const int inLength, int& outLength,
                                           const unsigned char* key, const unsigned char* iv)
{
	const auto noBlocks = inLength / AES_BLOCK_SIZE;

	auto* plainText = new unsigned char[noBlocks * AES_BLOCK_SIZE];

	outLength = 0;
	int temp;

	auto* ctx = EVP_CIPHER_CTX_new();

	EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), nullptr, key, iv);

	EVP_DecryptUpdate(ctx, plainText, &outLength, in, inLength);

	EVP_DecryptFinal_ex(ctx, plainText + outLength, &temp);
	outLength += temp;

	EVP_CIPHER_CTX_free(ctx);

	return unique_ptr<unsigned char>(plainText);
}

int writeToFile(const string& path, const unsigned char* content, const int length)
{
	ofstream file(path);
	if (!file.is_open())
	{
		return -1;
	}

	file.write((char*)content, length);

	return 0;
}

int main()
{
	const unsigned char shaToCompare[] = {
		0x8c,
		0xb4,
		0xda,
		0x05,
		0xd5,
		0xb5,
		0x98,
		0x36,
		0x62,
		0xaa,
		0xf0,
		0xea,
		0xfa,
		0xac,
		0xb7,
		0x64,
		0xf5,
		0xb4,
		0x89,
		0x42,
		0x22,
		0xf4,
		0xff,
		0xb8,
		0x65,
		0xb4,
		0x3d,
		0x01,
		0x29,
		0x5d,
		0x9f,
		0x99
	};
	const string directoryPath = "./Keys";

	unique_ptr<unsigned char> key(nullptr);

	for (const auto& entry : fs::directory_iterator((directoryPath)))
	{
		ifstream fileInput(entry.path());
		if (!fileInput.is_open())
		{
			cout << "error opening " << entry.path() << endl;
			return -1;
		}

		const auto fileSize = fs::file_size(entry.path());
		auto* buffer = new char[fileSize];

		fileInput.read(buffer, fileSize);

		auto fileDigest = computeSha256(buffer, fileSize);

		if (equalDigests(fileDigest.get(), shaToCompare))
		{
			cout << "Found file: " << entry.path() << endl;

			key.reset((unsigned char*)buffer);

			break;
		}

		delete[] buffer;
	}

	if (!key.get())
	{
		cout << "no key file found" << endl;
		return -1;
	}

	unique_ptr<unsigned char> iv(new unsigned char[AES_BLOCK_SIZE]);
	for (auto i = 0; i < AES_BLOCK_SIZE; ++i)
	{
		iv.get()[i] = 0x01;
	}

	ifstream accountFileInput("Accounts.txt");
	if (!accountFileInput.is_open())
	{
		cout << "couldn't open Accounts.txt" << endl;
		return -1;
	}
	const auto accountFileSize = fs::file_size("Accounts.txt");

	unique_ptr<char> accountFileContent(new char[accountFileSize]);

	accountFileInput.read(accountFileContent.get(), accountFileSize);

	int cipherTextLength = 0;
	auto cipherText = encryptAes256Cbc((unsigned char*)accountFileContent.get(), accountFileSize, cipherTextLength,
	                                   key.get(),
	                                   iv.get());

	if (writeToFile("Accounts_encrypted.bin", cipherText.get(), cipherTextLength))
	{
		cout << "error while writing encrypted content to file" << endl;
		return -1;
	}

	int plainTextLength = 0;
	auto plainText = decryptAes256Cbc(cipherText.get(), cipherTextLength, plainTextLength,
	                                  key.get(), iv.get());

	const auto originalFileDigest = computeSha256(accountFileContent.get(), accountFileSize);
	const auto restoredFileDigest = computeSha256(plainText.get(), plainTextLength);

	if (!equalDigests(originalFileDigest.get(), restoredFileDigest.get()))
	{
		cout << "the files don't match" << endl;
		return -1;
	}

	cout << "the files match" << endl;

	return 0;
}
