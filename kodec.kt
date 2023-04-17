import kotlin.math.pow

val CHECK_BITS : Array<Int> = arrayOf(1,2,4,8)
val error_level_from_0_to_10 : Int = 10

fun main() 
{
    var encode_chanel_resualt : String
    var decode_chanel_resualt : String
    
    val public_crypto_key : Array<Int> = arrayOf(997, 1343)
    val private_crypto_key : Array<Int> = arrayOf(1069, 1343)
    var encrypt_resualt : String
    var decrypt_resualt : String
    
    var hemming_encoded_result : String
    var hemming_encoded_with_error_result : String
    var hemming_decoded_result : String
    
    var msg : String
    
    
    msg = "Саня hui соси))00)))"
    
    print("Исходное сообщение: $msg\nКоличествро символов исходного сообщения: ")
    println(msg.length)
    
	encode_chanel_resualt = encode_chanel(msg)
    print("Кодированный канал: $encode_chanel_resualt\nКоличество бит в кодированном канале: ")
    println(encode_chanel_resualt.length)
    
    encrypt_resualt = encrypt(encode_chanel_resualt, public_crypto_key)
    print("Шифрованное сообщение: $encrypt_resualt\nКоличество бит в зашифрованном сообщении: ")
    println(encrypt_resualt.length)
    
    hemming_encoded_result = hemming_encode(encrypt_resualt)
    print("Добавление избыточности, кодирование Хэмминга: $hemming_encoded_result\nКоличество бит кодирования алгоритмом Хэминга: ")
    println(hemming_encoded_result.length)
    
    hemming_encoded_with_error_result = error_set(hemming_encoded_result)
    print("Добавление ошибок в кодирование Хэмминга: $hemming_encoded_with_error_result\nКоличество бит кодирования алгоритмом Хэминга с добавлением ошибок: ")
    println(hemming_encoded_with_error_result.length)
    
    hemming_decoded_result = hemming_decode(hemming_encoded_with_error_result)
    print("Избавление от избыточности, декодирование: $hemming_decoded_result\nКоличество бит декодирования алгоритмом Хэминга с исправлением ошибок: ")
	println(hemming_decoded_result.length)
    
    decrypt_resualt = decrypt(hemming_decoded_result, private_crypto_key)
    print("Расшифрованное сообщение: $decrypt_resualt\nКоличество бит в расшифрованном сообщении: ")
    println(decrypt_resualt.length)

    decode_chanel_resualt = decode_chanel(decrypt_resualt)
    print("Декодированный канал: $decode_chanel_resualt\nКоличество символов в декодированном канале: ")
    println(decode_chanel_resualt.length)
}

fun encode_chanel(text: String): String 
{
    var char_bin : String
    var text_bin : String = ""
    for(char in text.toCharArray())
        {
            char_bin = char.code.toString(2).padStart(11, '0')
            text_bin += char_bin
        }
	return text_bin
}

fun decode_chanel(encoded_text:String): String
{
    var text_str = ""
    for(i in 0..(encoded_text.length/11-1))
        {
        	text_str += encoded_text.substring(11*i,11*(i+1)).toInt(2).toChar()
        }
    return text_str
}
    
fun encrypt(encoded_text:String, key:Array<Int>): String
{
    var int_value_of_char : Int
    var iteration_exponentiation_module : Int
    var encrypted_text = ""
    for(i in 0..(encoded_text.length/11-1))
		{
            int_value_of_char = encoded_text.substring(11*i,11*(i+1)).toInt(2)
            iteration_exponentiation_module = int_value_of_char
            repeat(key[0]-1)
                {
                    iteration_exponentiation_module = (iteration_exponentiation_module * int_value_of_char)%key[1]
                }
            encrypted_text += iteration_exponentiation_module.toString(2).padStart(11, '0')
    	}
    return encrypted_text
}


fun decrypt(encrypted_text:String, key:Array<Int>): String
{
    var int_value_of_char : Int
    var iteration_exponentiation_module : Int
    var decrypted_text = ""
    for(i in 0..(encrypted_text.length/11-1))
        {
            int_value_of_char = encrypted_text.substring(11*i,11*(i+1)).toInt(2)
            iteration_exponentiation_module = int_value_of_char
            repeat(key[0]-1)
                {
                	iteration_exponentiation_module = (iteration_exponentiation_module * int_value_of_char)%key[1]
                }
            decrypted_text += iteration_exponentiation_module.toString(2).padStart(11, '0')
        }
    return decrypted_text
}

fun hemming_encode(text : String) : String
{
    var encoded_chunk : String
    var all_chunks : String = ""
    for(i in 0..text.length/11-1)
        {
            encoded_chunk = transform_chunk(text.substring(11*i,11*(1+i)))
            encoded_chunk = calculate_check_bits_values(encoded_chunk)
            all_chunks += encoded_chunk
        }
    return all_chunks
}

fun transform_chunk(text : String) : String
{
    var encoded_chunk : String = "0" + text 
    for (i in 1..3)
        {
        	encoded_chunk = encoded_chunk.substring(0,CHECK_BITS[i]-1) + "0" + encoded_chunk.substring(CHECK_BITS[i]-1,encoded_chunk.length)
        }
    return encoded_chunk
}

fun calculate_check_bits_values(text : String) : String
{ 
    var CHECK_BITS_VALUE : Array<Int> = arrayOf()
    var calc_check_bit_value : Int
    var encoded_chunk : String = text
        for (i in 0..3)
            {
                calc_check_bit_value = 0
                for (j in 1..15)
                    {
                    	calc_check_bit_value += (j.toString(2).padStart(4,'0').reversed()[i].toString().toInt()) and (encoded_chunk[j-1].toString().toInt())
                    }
                CHECK_BITS_VALUE += (calc_check_bit_value%2)
            }
        encoded_chunk = CHECK_BITS_VALUE[0].toString() + encoded_chunk.substring(CHECK_BITS[0],encoded_chunk.length)
        for (i in 1..3)
            {
                encoded_chunk = encoded_chunk.substring(0,CHECK_BITS[i]-1) + CHECK_BITS_VALUE[i].toString() + encoded_chunk.substring(CHECK_BITS[i],encoded_chunk.length)
            }
    return encoded_chunk
}

fun error_set(text : String) : String
{
    var result : String = ""
    var bit_index : Int
    var chunk : String
    for(i in 0..text.length/15-1)
        {
            chunk = text.substring(15*i,15*(i+1))
            if (((0..10).random()) < (error_level_from_0_to_10))
                {
                    bit_index = (2..14).random()
                    chunk = chunk.substring(0,bit_index-1) + (chunk[bit_index].toString().toInt() xor 1).toString() + chunk.substring(bit_index,15)
                } 
            result += chunk
        }
    return result
}

fun hemming_decode(text : String) : String
{
    var encoded_chunk : String
    var msg : String = ""
    var error_bit : Int
    var error_bit_list : String = ""
    var count_bits_in_error_list : Int = 0
    for(i in 0..text.length/15-1)
        {
            error_bit = 0
            encoded_chunk = transform_chunk(text.substring(15*i+2,15*i+3) + text.substring(15*i+4,15*i+7) + text.substring(15*i+8,15*i+15))
            encoded_chunk = calculate_check_bits_values(encoded_chunk)
            for(j in 0..3)
                {
                    if(text.substring(15*i,15*(i+1))[CHECK_BITS[j]-1]!=encoded_chunk[CHECK_BITS[j]-1])
                    	error_bit += 2.toFloat().pow(j).toInt()
                }
            if ((error_bit!=0) and (error_bit<16))
                {
                	error_bit_list += (15*i+error_bit).toString() + ", "
                    count_bits_in_error_list += 1
                    if (error_bit==1)
                    	encoded_chunk = (encoded_chunk.toString().toInt() xor 1).toString() + text.substring(1,15)
                    else if (error_bit==15)
                    	encoded_chunk = encoded_chunk.substring(0,14) + (text[14].toString().toInt() xor 1).toString()
                    else
                    	encoded_chunk = encoded_chunk.substring(0,error_bit-1) + (encoded_chunk[error_bit-1].toString().toInt() xor 1).toString() + encoded_chunk.substring(error_bit,15)
                }
            msg += encoded_chunk[2].toString() + encoded_chunk.substring(4,7) + encoded_chunk.substring(8,15)
        }
        if (count_bits_in_error_list==0)
        	println("Количество ошибок : 0")
        else
            {
                error_bit_list = error_bit_list.substring(0,error_bit_list.length-2)
                println("Ошибки в битах с индексом: $error_bit_list")
                println("Количество ошибок : $count_bits_in_error_list")
            }
    return msg
}
