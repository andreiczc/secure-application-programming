#include <iostream>
#include <openssl/ssl.h>
#include <openssl/bio.h>

using namespace std;

#define len(str) (strlen((char*)str) + 1)

int main()
{
	unsigned char key[] = {
		/* Need all 32 bytes... */
		0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
		0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
		0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
		0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F
	};

	unsigned char originalMessage[] = "hello";

	EVP_CIPHER_CTX aesEncrypt;
	EVP_CIPHER_CTX_init(&aesEncrypt);
	EVP_EncryptInit(&aesEncrypt, EVP_aes_256_ecb(), key, nullptr);

	const auto blockSize = EVP_CIPHER_CTX_block_size(&aesEncrypt);

	const int lastBlockSize = len(originalMessage) % blockSize;

	const int noBlocks = lastBlockSize
		                     ? (len(originalMessage)) / blockSize + 1
		                     : (len(originalMessage)) / blockSize;

	auto* cipherText = new unsigned char[noBlocks * blockSize];

	int bytesWritten = 0;
	for (auto i = 0; i < noBlocks; ++i)
	{
		EVP_EncryptUpdate(&aesEncrypt, &cipherText[i * blockSize], &bytesWritten,
		                  (unsigned char*)(&originalMessage[i * blockSize]),
		                  i == (noBlocks - 1) ? lastBlockSize : blockSize);
	}

	EVP_EncryptFinal(&aesEncrypt, &cipherText[noBlocks - 1], &bytesWritten);

	EVP_CIPHER_CTX aesDecrypt;
	EVP_CIPHER_CTX_init(&aesDecrypt);
	EVP_DecryptInit(&aesDecrypt, EVP_aes_256_ecb(), key, nullptr);

	// fix this for messages with multiple blocks
	auto* plainText = new unsigned char[noBlocks * blockSize];
	for (auto i = 0; i < noBlocks; ++i)
	{
		EVP_DecryptUpdate(&aesDecrypt, &plainText[i * blockSize], &bytesWritten, &cipherText[i * blockSize], blockSize);
	}
	EVP_DecryptFinal(&aesDecrypt, &plainText[noBlocks - 1], &bytesWritten);

	cout << plainText;

	EVP_CIPHER_CTX_cleanup(&aesDecrypt);
	EVP_CIPHER_CTX_cleanup(&aesEncrypt);

	return 0;
}
