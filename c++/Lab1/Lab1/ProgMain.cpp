#include <iostream>
#include <openssl/md5.h>
#include <openssl/sha.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <openssl/conf.h>
#include <openssl/aes.h>

using namespace std;

#define strlenu(var) strlen((char*)var)

unique_ptr<unsigned char> computeMD5(const void* originalMessage, const int messageSize)
{
	unique_ptr<unsigned char> digest(new unsigned char[MD5_DIGEST_LENGTH]);

	MD5_CTX ctx;
	MD5_Init(&ctx);

	if (!MD5_Update(&ctx, originalMessage, messageSize) || !MD5_Final(digest.get(), &ctx))
	{
		digest.reset(nullptr);
		printf("error computing MD5\n");
	}

	return digest;
}

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

unique_ptr<unsigned char> encryptAes128Cbc(const unsigned char* in, const int inLength, int& outLength,
                                           const unsigned char* key, const unsigned char* iv)
{
	int noBlocks = inLength % AES_BLOCK_SIZE ? inLength / AES_BLOCK_SIZE + 1 : inLength / AES_BLOCK_SIZE;

	auto* cipherText = new unsigned char[AES_BLOCK_SIZE * noBlocks];

	outLength = 0;
	int temp = 0;

	auto* ctx = EVP_CIPHER_CTX_new();

	EVP_EncryptInit_ex(ctx, EVP_aes_128_cbc(), nullptr, key, iv);

	EVP_EncryptUpdate(ctx, cipherText, &outLength, in, inLength);

	EVP_EncryptFinal_ex(ctx, cipherText + outLength, &temp);
	outLength += temp;

	EVP_CIPHER_CTX_free(ctx);

	return unique_ptr<unsigned char>(cipherText);
}

unique_ptr<unsigned char> decryptAes128Cbc(const unsigned char* in, const int inLength, int& outLength,
                                           const unsigned char* key, const unsigned char* iv)
{
	int noBlocks = inLength / AES_BLOCK_SIZE;

	auto* plainText = new unsigned char[noBlocks * AES_BLOCK_SIZE];

	outLength = 0;
	int temp;

	auto* ctx = EVP_CIPHER_CTX_new();

	EVP_DecryptInit_ex(ctx, EVP_aes_128_cbc(), nullptr, key, iv);

	EVP_DecryptUpdate(ctx, plainText, &outLength, in, inLength);

	EVP_DecryptFinal_ex(ctx, plainText + outLength, &temp);
	outLength += temp;

	EVP_CIPHER_CTX_free(ctx);

	plainText[outLength] = '\0';

	return unique_ptr<unsigned char>(plainText);
}

int main()
{
	unsigned char originalMessage[] =
		"hello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello world";

	unsigned char password[] = "passwordpasswor";
	unsigned char iv[] = "passwordpasswor";

	int cipherTextLength = 0;
	int plainTextLength = 0;

	auto cipherText = encryptAes128Cbc(originalMessage, strlenu(originalMessage), cipherTextLength, password, iv);
	auto plainText = decryptAes128Cbc(cipherText.get(), cipherTextLength, plainTextLength, password, iv);

	cout << plainText.get();

	return 0;
}
