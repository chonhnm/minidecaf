	.text
	.globl	main
main:
	addi sp, sp, -16
	sw ra, 12(sp)
	sw fp, 8(sp)
	addi fp, sp, 16
	li t1,1
	addi sp,sp,-4
	sw t1, 0(sp)
	addi sp, sp, -4
	addi t1, fp, -12
	sw t1, 0(sp)
	lw t1, 4(sp)
	lw t2, 0(sp)
	addi sp, sp, 4
	sw t1, 0(t2)
	addi sp, sp, 4
	li t1,2
	addi sp,sp,-4
	sw t1, 0(sp)
	addi sp, sp, -4
	addi t1, fp, -16
	sw t1, 0(sp)
	lw t1, 4(sp)
	lw t2, 0(sp)
	addi sp, sp, 4
	sw t1, 0(t2)
	addi sp, sp, 4
	addi sp, sp, -4
	addi t1, fp, -12
	sw t1, 0(sp)
	lw t1, 0(sp)
	lw t1, 0(t1)
	sw t1, 0(sp)
	addi sp, sp, -4
	addi t1, fp, -16
	sw t1, 0(sp)
	lw t1, 0(sp)
	lw t1, 0(t1)
	sw t1, 0(sp)
	lw t2, 0(sp)
	addi sp,sp,4
	lw t1, 0(sp)
	addi sp,sp,4
	add t1,t1,t2
	addi sp,sp,-4
	sw t1, 0(sp)
	jal t1, main_epilogue
	li t1,0
	addi sp,sp,-4
	sw t1, 0(sp)
	main_epilogue:
	lw a0, 0(sp)
	addi sp, sp, 4
	lw fp, 8(sp)
	lw ra, 12(sp)
	addi sp, sp, 16
	jr ra
