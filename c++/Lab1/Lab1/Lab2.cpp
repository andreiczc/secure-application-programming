#include <iostream>
#include <openssl/aes.h>
#include <cstring>

using namespace std;

int main()
{
	unsigned char input[] = "hellohellohellohello";
	unsigned char userPassword[] = "passwordpasswor";
	unsigned char iv[] = "passwordpasswor";
	unsigned char iv2[] = "passwordpasswor";


	const auto originalInputLength = strlen((char*)input) + 1;
	auto inputLength = originalInputLength;

	const auto originalOutputLength = (inputLength / 16 + 1) * 16;
	auto outputLength = originalOutputLength;

	auto* outputBuffer = new unsigned char[outputLength];

	AES_KEY encryptionKey;
	AES_set_encrypt_key(userPassword, 128, &encryptionKey);

	int currLength = outputLength > 16 ? 16 : outputLength;
	outputLength -= currLength;

	for (auto i = 0; i < originalOutputLength / 16; ++i)
	{
		AES_cbc_encrypt(&(input[i * 16]), &(outputBuffer[i * 16]), currLength, &encryptionKey, iv, AES_ENCRYPT);

		currLength = outputLength > 16 ? 16 : outputLength;
		outputLength -= currLength;
	}

	auto* testBuffer = new unsigned char[inputLength];

	AES_KEY decryptionKey;
	AES_set_decrypt_key(userPassword, 128, &decryptionKey);


	currLength = inputLength > 16 ? 16 : inputLength;
	inputLength -= currLength;
	for (auto i = 0; i < originalInputLength / 16 + 1; ++i)
	{
		AES_cbc_encrypt(&(outputBuffer[i * 16]), &(testBuffer[i * 16]), currLength, &decryptionKey, iv2, AES_DECRYPT);

		currLength = inputLength > 16 ? 16 : inputLength;
		inputLength -= currLength;
	}

	cout << testBuffer;

	return 0;
}
